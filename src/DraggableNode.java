import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DraggableNode extends AnchorPane {


	private RootLayout layout;
	@FXML AnchorPane root_pane;
	@FXML Circle top;
	@FXML Circle bottom;
	@FXML Circle left;
	@FXML Circle right;
	ObjectModel model;
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDropped;
	private DragIconType mType = null;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	@FXML private VBox element;
	@FXML private TextArea text;
	@FXML private Button closeButton;
	private final DraggableNode self;



	//Single constructor
		public DraggableNode(RootLayout lay) {
			
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("resources/DraggableNode.fxml")
					);
			
			fxmlLoader.setRoot(this); 
			fxmlLoader.setController(this);
			
			self = this;
			
			try { 
				fxmlLoader.load();
	        
			} catch (IOException exception) {
			    throw new RuntimeException(exception);
			}
			layout = lay;
		}


    //Block of initial settings
		@FXML
		private void initialize() {
			this.getStylesheets().add(getClass().getResource("resources/textCenter.css").toExternalForm());
			buildNodeDragHandlers();
		}



		public void relocateToPoint (Point2D p) {

			//relocates the object to a point that has been converted to
			//scene coordinates
			Point2D localCoords = getParent().sceneToLocal(p);
			
			relocate ( 
					(int) (localCoords.getX() - mDragOffset.getX()),
					(int) (localCoords.getY() - mDragOffset.getY())
				);
		}


		
		public DragIconType getType () { return mType; }


		
		public void setType (DragIconType type) {
			
			mType = type;
			model = new ObjectModel(mType);
			getStyleClass().clear();
			
			switch (mType) {
				case start:
					getStyleClass().add("start");
					getStyleClass().add("startEndSize");
					Text text0 = new Text("Start");
					text0.fontProperty().setValue(Font.font(30));
					AnchorPane.setTopAnchor(text0,Double.valueOf(18));
					AnchorPane.setLeftAnchor(text0,Double.valueOf(38));
					root_pane.getChildren().add(text0);
					bottom.setRadius(5);
					bottom.setFill(Color.BLACK);
					bottom.setCenterX(root_pane.getPrefWidth()/2);
					bottom.setCenterY(root_pane.getPrefHeight());
					break;
				case end :
					getStyleClass().add("end");
					getStyleClass().add("startEndSize");
					Text text1 = new Text("End");
					text1.fontProperty().setValue(Font.font(30));
					AnchorPane.setTopAnchor(text1,Double.valueOf(18));
					AnchorPane.setLeftAnchor(text1,Double.valueOf(47));
					root_pane.getChildren().add(text1);
					top.setRadius(5);
					top.setFill(Color.BLACK);
					top.setCenterX(root_pane.getPrefWidth()/2);
					top.setCenterY(-1);
					break;
			case rectangle:
				getStyleClass().add("dragicon");
					getStyleClass().add("rectangleSize");
				top.setRadius(5);
				top.setFill(Color.BLACK);
				top.setCenterX(root_pane.getPrefWidth()/2);
				top.setCenterY(-1);
				bottom.setRadius(5);
				bottom.setFill(Color.BLACK);
				bottom.setCenterX(root_pane.getPrefWidth()/2);
				bottom.setCenterY(root_pane.getPrefHeight()+1);
				left.setRadius(5);
				left.setFill(Color.BLACK);
				left.setCenterX(-1);
				left.setCenterY(root_pane.getPrefHeight()/2);
				right.setRadius(5);
				right.setFill(Color.BLACK);
				right.setCenterX(root_pane.getPrefWidth()+1);
				right.setCenterY(root_pane.getPrefHeight()/2);
					break;

				case rhomb:
					getStyleClass().add("dragicon");
					getStyleClass().add("rhomb");
					getStyleClass().add("rhombSize");
					top.setRadius(5);
					top.setFill(Color.BLACK);
					top.setCenterX(10);
					top.setCenterY(10);
					bottom.setRadius(5);
					bottom.setFill(Color.BLACK);
					bottom.setCenterX(115);
					bottom.setCenterY(115);
					left.setRadius(5);
					left.setFill(Color.BLACK);
					left.setCenterX(10);
					left.setCenterY(115);
					right.setRadius(5);
					right.setFill(Color.BLACK);
					right.setCenterX(115);
					right.setCenterY(10);
					break;
			
			default:
			break;
			}
			if (!getType().equals(DragIconType.end)&& !getType().equals(DragIconType.start)) text.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
						if(mouseEvent.getClickCount() == 2){
							System.out.println("Edit");
							text.setEditable(true);
						}
					}
				}
			});
            addOnClickedCirlce(bottom);
            addOnClickedCirlce(top);
            addOnClickedCirlce(left);
            addOnClickedCirlce(right);
		}



	     private void addOnClickedCirlce(Circle circle){
			 circle.setOnMouseClicked(event -> {
				if (layout.isFirstTarget()){
//					if (getType().equals(DragIconType.rectangle)){
//					}

					Point2D localCoords = getParent().sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()));
					Line line = new Line(localCoords.getX(),localCoords.getY(),localCoords.getX(),localCoords.getY());
					line.setStrokeWidth(2);
                    layout.startDrawLine(line);
                    layout.setFirstTarget(false);
					event.consume();
				}else {
//                    layout.setMouseMoveEvent();
                    layout.setFirstTarget(true);
                }
			});
	       }


		
		public void buildNodeDragHandlers() {
			
			mContextDragOver = new EventHandler <DragEvent>() {

				//dragover to handle node dragging in the right pane view
				@Override
				public void handle(DragEvent event) {		
			
					event.acceptTransferModes(TransferMode.ANY);				
					relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

					event.consume();
				}
			};
			
			//dragdrop for node dragging
			mContextDragDropped = new EventHandler <DragEvent> () {
		
				@Override
				public void handle(DragEvent event) {
				
					getParent().setOnDragOver(null);
					getParent().setOnDragDropped(null);
					
					event.setDropCompleted(true);
					
					event.consume();
				}
			};

			//close button click
			closeButton.setOnMouseClicked(new EventHandler <MouseEvent> () {

				@Override
				public void handle(MouseEvent event) {
					AnchorPane parent  = (AnchorPane) self.getParent();
					parent.getChildren().remove(self);
				}

			});

			//drag detection for node dragging
			root_pane.setOnDragDetected ( new EventHandler <MouseEvent> () {

				@Override
				public void handle(MouseEvent event) {
				
					getParent().setOnDragOver(null);
					getParent().setOnDragDropped(null);

					getParent().setOnDragOver (mContextDragOver);
					getParent().setOnDragDropped (mContextDragDropped);

	                //begin drag ops
	                mDragOffset = new Point2D(event.getX(), event.getY());
	                
	                relocateToPoint(
	                		new Point2D(event.getSceneX(), event.getSceneY())
	                		);
	                
	                ClipboardContent content = new ClipboardContent();
					DragContainer container = new DragContainer();
					
					container.addData ("type", mType.toString());
					content.put(DragContainer.AddNode, container);
					
	                startDragAndDrop (TransferMode.ANY).setContent(content);                
	                
	                event.consume();					
				}
				
			});		
		}		
}
