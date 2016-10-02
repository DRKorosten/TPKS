
import java.io.IOException;
import java.util.Iterator;

import com.sun.javafx.geom.Path2D;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class RootLayout extends AnchorPane{

	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;

	private DragIcon mDragOverIcon = null;
	
	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;
	
	public RootLayout() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("resources/RootLayout.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}
	}
	
	@FXML
	private void initialize() {
		
		//Add one icon that will be used for the drag-drop process
		//This is added as a child to the root anchorpane so it can be visible
		//on both sides of the split pane.
		mDragOverIcon = new DragIcon();
		mDragOverIcon.setVisible(false);
		mDragOverIcon.setOpacity(0.65);
		getChildren().add(mDragOverIcon);
		
		//populate left pane with multiple colored icons for testing
		for (int i = 0; i < 2; i++) {
			DragIcon icn = new DragIcon();
			addDragDetection(icn);
			icn.setType(DragIconType.values()[i]);

			left_pane.getChildren().add(icn);
		}
		ToggleButton linker = new ToggleButton("----------");
		linker.setOnMouseClicked(event -> {
			if (linker.isSelected())  {
					right_pane.setStyle("-fx-background-color: red");
			} else {
					right_pane.setStyle("-fx-background-color: transparent");
			}
		});
//		linker.getStyleClass().add("linker");
		    left_pane.getChildren().add(linker);
		buildDragHandlers();
//        right_pane.setOnMouseClicked(event -> {
////
//            right_pane.getChildren().addAll(new GraphicsContext(){
//
//            });
//        });
//		right_pane.setOnMouseClicked(event -> {
//			Iterator iter = right_pane.getChildren().iterator();
//			while (iter.hasNext()){
//				Object object = iter.next();
//				if (object instanceof DraggableNode)
//				((DraggableNode) iter.next()).setEditableFalse();
//			}
//		});
	}
//    void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
//        gc.setFill(Color.BLACK);
//        double ARR_SIZE = 8;
//        double dx = x2 - x1, dy = y2 - y1;
//        double angle = Math.atan2(dy, dx);
//        int len = (int) Math.sqrt(dx * dx + dy * dy);
//
//        Transform transform = Transform.translate(x1, y1);
//        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
//        gc.setTransform(new Affine(transform));
//
//        gc.strokeLine(0, 0, len, 0);
//        gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
//                4);
//    }
	
	private void addDragDetection(DragIcon dragIcon) {
		
		dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				// set drag event handlers on their respective objects
				base_pane.setOnDragOver(mIconDragOverRoot);
				right_pane.setOnDragOver(mIconDragOverRightPane);
				right_pane.setOnDragDropped(mIconDragDropped);
				
				// get a reference to the clicked DragIcon object
				DragIcon icn = (DragIcon) event.getSource();
				
				//begin drag ops
				mDragOverIcon.setType(icn.getType());
				mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
            
				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();
				
				container.addData ("type", mDragOverIcon.getType().toString());
				content.put(DragContainer.AddNode, container);

				mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
				mDragOverIcon.setVisible(true);
				mDragOverIcon.setMouseTransparent(true);
				event.consume();					
			}
		});
	}	
	
	private void buildDragHandlers() {
		
		//drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				//turn on transfer mode and track in the right-pane's context 
				//if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {
					
					event.acceptTransferModes(TransferMode.ANY);
					mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};
		
		mIconDragOverRightPane = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);
				
				//convert the mouse coordinates to scene coordinates,
				//then convert back to coordinates that are relative to 
				//the parent of mDragIcon.  Since mDragIcon is a child of the root
				//pane, coodinates must be in the root pane's coordinate system to work
				//properly.
				mDragOverIcon.relocateToPoint(
								new Point2D(event.getSceneX(), event.getSceneY())
				);
				event.consume();
			}
		};
				
		mIconDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {
				
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				container.addData("scene_coords", 
						new Point2D(event.getSceneX(), event.getSceneY()));
				
				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);
				
				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};
		
		this.setOnDragDone (new EventHandler <DragEvent> (){
			
			@Override
			public void handle (DragEvent event) {
				
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
								
				mDragOverIcon.setVisible(false);
				
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				if (container != null) {
					if (container.getValue("scene_coords") != null) {
					
						DraggableNode node = new DraggableNode();
						
						node.setType(DragIconType.valueOf(container.getValue("type")));
						right_pane.getChildren().add(node);

						Point2D cursorPoint = container.getValue("scene_coords");

						node.relocateToPoint(
								new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
								);
					}
				}
				
				container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.DragNode);
				
				if (container != null) {
					if (container.getValue("type") != null)
						System.out.println ("Moved node " + container.getValue("type"));
				}
				
				event.consume();
			}
		});		
	}
}
