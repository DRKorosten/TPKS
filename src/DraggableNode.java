import java.io.IOException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	 DragIconType mType = null;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	@FXML private VBox element;
	@FXML private TextField text;
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
			text.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.length()<9){
					text.setText(newValue);
					model.setText(newValue);
				}else text.setText(oldValue);
			});
			buildNodeDragHandlers();
		}



		public void relocateToPoint (Point2D p) {

			//relocates the object to a point that has been converted to
			//scene coordinates
			Point2D localCoords = getParent().sceneToLocal(p);
			model.setLayout(new Point2D((localCoords.getX() - mDragOffset.getX()),(localCoords.getY() - mDragOffset.getY())));
			relocate ( 
					(int) (localCoords.getX() - mDragOffset.getX()),
					(int) (localCoords.getY() - mDragOffset.getY())
				);
		}


		
		public DragIconType getType () { return mType; }

        public DraggableNode(RootLayout lay,ObjectModel sourse){
			this(lay);
			setType(sourse.type);
			model = sourse;
			text.setText(sourse.text);
			setLayoutX(sourse.layoutX);
			setLayoutY(sourse.layoutY);

		}
		
		public void setType (DragIconType type) {
			
			mType = type;
			model = new ObjectModel(mType,new Point2D(this.getLayoutX(),this.getLayoutY()));
			layout.addModel(model);
			getStyleClass().clear();
			
			switch (mType) {
				case start:
					getStyleClass().add("start");
					this.setPrefSize(150,40);
					Text text0 = new Text("Start");
					text0.fontProperty().setValue(Font.font(30));
					AnchorPane.setTopAnchor(text0,Double.valueOf(0));
					AnchorPane.setLeftAnchor(text0,Double.valueOf(38));
					root_pane.getChildren().add(text0);
					bottom.setRadius(5);
					bottom.setFill(Color.BLACK);
					bottom.setCenterX(root_pane.getPrefWidth()/2);
					bottom.setCenterY(root_pane.getPrefHeight()+2);
					break;
				case end :
					getStyleClass().add("end");
					this.setPrefSize(150,40);
					Text text1 = new Text("End");
					text1.fontProperty().setValue(Font.font(30));
					AnchorPane.setTopAnchor(text1,Double.valueOf(0));
					AnchorPane.setLeftAnchor(text1,Double.valueOf(47));
					root_pane.getChildren().add(text1);
					top.setRadius(5);
					top.setFill(Color.BLACK);
					top.setCenterX(root_pane.getPrefWidth()/2);
					top.setCenterY(-2);
					break;
			case rectangle:
				getStyleClass().add("dragicon");
					getStyleClass().add("rectangleSize");
				top.setRadius(5);
				top.setFill(Color.BLACK);
				top.setCenterX(root_pane.getPrefWidth()/2);
				top.setCenterY(-2);
				bottom.setRadius(5);
				bottom.setFill(Color.BLACK);
				bottom.setCenterX(root_pane.getPrefWidth()/2);
				bottom.setCenterY(root_pane.getPrefHeight()+2);
					break;

				case rhomb:
					getStyleClass().add("dragicon");
					getStyleClass().add("rhomb");
					top.setRadius(5);
					top.setFill(Color.BLACK);
					top.setCenterX(root_pane.getPrefWidth()/2);
					top.setCenterY(-2);
					left.setRadius(5);
					left.setFill(Color.BLACK);
					left.setCenterX(-1);
					left.setCenterY(root_pane.getPrefHeight()/2);
					right.setRadius(5);
					right.setFill(Color.BLACK);
					right.setCenterX(root_pane.getPrefWidth()+2);
					right.setCenterY(root_pane.getPrefHeight()/2);
					break;
			
			default:
			break;
			}
			if (!getType().equals(DragIconType.end)&& !getType().equals(DragIconType.start)) text.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
						if(mouseEvent.getClickCount() == 2){
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

		public  ObjectModel getModel(){
			return model;
		}


	     private void addOnClickedCirlce(Circle circle){
			 circle.setOnMouseClicked(event -> {
//				    if (!layout.isFirstTarget()){
//						model.addEntry(getType(circle));
//					}else {
//
//					}
					layout.drawLine(circle);
					event.consume();
			});
	       }

	       public LinkSideType getType(Circle circle){
			   if (circle.getCenterX()>0 && circle.getCenterY()>0 ){
				   if (circle.getCenterY()>this.getPrefHeight()){
					   return LinkSideType.bottom;
				   }else {
					   return LinkSideType.right;
				   }
			   }else {
				   if (circle.getCenterY()>0){
					   return LinkSideType.left;
				   }else {
					   return LinkSideType.top;
				   }
			   }
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
					layout.removeModel(model);

					ArrayList<Line> l = new ArrayList<Line>();

					for (Node node : parent.getChildren()) {

						if (node instanceof Line) {

							double xs = ((Line) node).getStartX() - root_pane.getLayoutX();
							double ys = ((Line) node).getStartY() - root_pane.getLayoutY() ;
							double xe = ((Line) node).getEndX() - root_pane.getLayoutX();
							double ye = ((Line) node).getEndY() - root_pane.getLayoutY();
							boolean fromStart = false;
							if((fromStart = top.contains(xs,ys)) || top.contains(xe,ye)){
								setNoneValue((Line)node,fromStart, LinkSideType.top);
								l.add((Line)node);

							}else
							if(fromStart = bottom.contains(xs,ys) ||
									bottom.contains(xe,ye)){
								setNoneValue((Line)node,fromStart, LinkSideType.bottom);
								l.add((Line)node);
							} else
							if(fromStart = right.contains(xs,ys) ||
									right.contains(xe,ye)){
								setNoneValue((Line)node,fromStart, LinkSideType.right);
								l.add((Line)node);

							}else
							if(fromStart = left.contains(xs,ys) ||
									left.contains(xe,ye)) {
								setNoneValue((Line)node,fromStart, LinkSideType.left);
								l.add((Line) node);
							}
							}
					}


					for (Node node : l){
						layout.deleteLink((Line)node);
					}
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

	private void setNoneValue(Line line, boolean start, LinkSideType type) {
//		for (:
//			 ) {
//
//		}
	}

	/**
	 * check does this point in circle
	 * @param xin
	 * @param yin
	 * @return
	 */
	public boolean con(double xin, double yin){
		double x = xin - root_pane.getLayoutX();
		double y = yin - root_pane.getLayoutY() ;
		if(
				top.contains(x,y)||bottom.contains(x,y)||right.contains(x,y)||left.contains(x,y)
				) return true;
		return false;
	}
	/**
	 * check this point in which circle
	 * @param xin
	 * @param yin
	 * @return
	 */
	public LinkSideType whatCircleContain(double xin, double yin){
		double x = xin - root_pane.getLayoutX();
		double y = yin - root_pane.getLayoutY() ;
		if(top.contains(x,y)){
			return LinkSideType.top;
		}else if(bottom.contains(x,y)){
			return LinkSideType.bottom;
		}else if(right.contains(x,y)){
			return LinkSideType.right;
		}else
			return LinkSideType.left;

	}
}
