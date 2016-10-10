import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;

public class RootLayout extends AnchorPane{
	//Local variables
	private boolean isFirstTarget = true;
	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;
	private DragIcon mDragOverIcon;
	private Line line;
	private EventHandler<DragEvent> mIconDragOverRoot;
	private EventHandler<DragEvent> mIconDragDropped;
	private EventHandler<DragEvent> mIconDragOverRightPane;


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

		//create all items on left pane
		for (int i = 0; i < 4; i++) {
			DragIcon icn = new DragIcon();
			addDragDetection(icn);
			icn.setType(DragIconType.values()[i]);
			left_pane.getChildren().add(icn);
		}
		buildDragHandlers();
	}


	public void startDrawLine(Line line){
		if (isFirstTarget) {
			right_pane.getChildren().add(line);
			right_pane.setOnMouseMoved(event -> {
						line.setEndX(event.getX());
						line.setEndY(event.getY());
			});
			line.setOnMouseClicked(event -> {
				if (event.getClickCount()==2){
					right_pane.getChildren().remove(line);
				}
			});
			right_pane.setOnMouseClicked(event -> right_pane.setOnMouseMoved(null));
		}
		else {

		}
	}

	
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

		RootLayout t = this;

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
						if (!DragIconType.valueOf(container.getValue("type")).equals(DragIconType.start) && !DragIconType.valueOf(container.getValue("type")).equals(DragIconType.end)) {
							DraggableNode node = new DraggableNode(t);

							node.setType(DragIconType.valueOf(container.getValue("type")));
							right_pane.getChildren().add(node);

							Point2D cursorPoint = container.getValue("scene_coords");

							node.relocateToPoint(
									new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
							);
						}else{
							for (Node node : right_pane.getChildren()
								 ) {
								if (node instanceof DraggableNode)
								if (((DraggableNode)(node)).getType().equals(DragIconType.valueOf(container.getValue("type")))){
									return;
								}
							}
							DraggableNode node = new DraggableNode(t);

							node.setType(DragIconType.valueOf(container.getValue("type")));
							right_pane.getChildren().add(node);

							Point2D cursorPoint = container.getValue("scene_coords");

							node.relocateToPoint(
									new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
							);
						}
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
	public boolean isFirstTarget() {
		return isFirstTarget;
	}

	public void setFirstTarget(boolean firstTarget) {
		isFirstTarget = firstTarget;
	}


}
