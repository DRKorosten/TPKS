
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DragIcon extends AnchorPane{
	
	@FXML AnchorPane root_pane;
	private DragIconType mType = null;
	

	public DragIcon() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("resources/DragIcon.fxml")
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
	private void initialize() {}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
				(int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
			);
	}
	
	public DragIconType getType () { return mType; }
	
	public void setType (DragIconType type) {
		
		mType = type;
		getStyleClass().clear();

		
		switch (mType) {
			case start:
				getStyleClass().add("start");
				getStyleClass().add("smallSize");
				Text text = new Text("Start");
				AnchorPane.setTopAnchor(text,Double.valueOf(7));
				AnchorPane.setLeftAnchor(text,Double.valueOf(17));
				root_pane.getChildren().add(text);
				break;
			case end:
				getStyleClass().add("end");
				getStyleClass().add("smallSize");
				Text text1 = new Text("End");
				AnchorPane.setTopAnchor(text1,Double.valueOf(7));
				AnchorPane.setLeftAnchor(text1,Double.valueOf(20));
				root_pane.getChildren().add(text1);
				break;
			case rectangle:
				getStyleClass().add("dragicon");
				break;
		
            case rhomb:
				getStyleClass().add("dragicon");
                getStyleClass().add("rhomb");
		        break;
		default:
		break;
		}
	}
}
