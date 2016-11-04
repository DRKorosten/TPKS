import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class LineArrow extends Line implements ChangeListener{
    final static double SIZE =15;
   private Line line1;
   private Line line2;

    public LineArrow(){
       super();
        line1 = new Line();
        line1.setStroke(Color.GREEN);
        line1.setStrokeWidth(3);
        line2 = new Line();
        line2.setStroke(Color.GREEN);
        line2.setStrokeWidth(3);

    }
    public void setArrowCoordinates(){
        line1.setEndX(super.getEndX());
        line1.setEndY(super.getEndY());
        line2.setEndX(super.getEndX());
        line2.setEndY(super.getEndY());
        double[] coordinates = getCoordinates();
        double x3 = coordinates[0];
        double y3 = coordinates[1];
        double x2 = coordinates[2];
        double y2 = coordinates[3];
        line1.setStartX(x3 + super.getEndX());
        line1.setStartY(super.getEndY()+y3);
        line2.setStartX(x2 + super.getEndX());
        line2.setStartY(super.getEndY()+y2);

    }

    public void linkArrow(){
        super.endXProperty().addListener(this);
        super.endYProperty().addListener(this);
        super.startXProperty().addListener(this);
        super.startYProperty().addListener(this);
        line1.endXProperty().bind(super.endXProperty());
        line1.endYProperty().bind(super.endYProperty());
        line2.endXProperty().bind(super.endXProperty());
        line2.endYProperty().bind(super.endYProperty());
    }

    public double[] getCoordinates(){
        double x = super.getEndX()-super.getStartX();
        double y = super.getEndY()-super.getStartY();
        double lenght = Math.sqrt(x*x+y*y);
        double x1 = x/lenght*SIZE;
        double y1 = y/lenght*SIZE;
        double x2 = x1*Math.cos(-Math.PI/12-Math.PI) - y1*Math.sin(-Math.PI/12-Math.PI);
        double y2 = x1*Math.sin(-Math.PI/12-Math.PI) + y1*Math.cos(-Math.PI/12-Math.PI);
        double x3 = x1*Math.cos(Math.PI+Math.PI/12) - y1*Math.sin(Math.PI+Math.PI/12);
        double y3 = x1*Math.sin(Math.PI+Math.PI/12) + y1*Math.cos(Math.PI+Math.PI/12);
        double[] coodr = new double[]{x3,y3,x2,y2};
        return coodr;
    }
    public Line getLine1() {
        return line1;
    }

    public Line getLine2() {
        return line2;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        line1.startXProperty().set(getCoordinates()[0]+super.getEndX());
        line2.startXProperty().set(getCoordinates()[2]+super.getEndX());
        line1.startYProperty().set(getCoordinates()[1]+super.getEndY());
        line2.startYProperty().set(getCoordinates()[3]+super.getEndY());
    }
}
