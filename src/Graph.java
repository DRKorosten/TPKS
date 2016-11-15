import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by Dmytro on 01.11.16.
 */
public class Graph extends Application {
    final static double RADIUS_FROM_CENTER = 150;
    final static double RADIUS_CIRCLE = 40;
    final static double WIDTH = 800;
    final static double HEIGTH = 600;
    private String[][] matrix;
    private String[] names;
    private ArrayList<Circle> nodes = new ArrayList<>();

    public Graph(String[][] matrix,String[] names){
        this.matrix = matrix;
        this.names = names;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane root = new AnchorPane();
        primaryStage.setTitle("Graph");
        root.setOnMouseClicked(event -> {
            System.out.println(event.getX()+" "+event.getY());
        });
        double difAngle = (Math.PI*2/(double)names.length);
        double angle = difAngle;
        Point2D center = new Point2D(WIDTH/2,HEIGTH/2);
        for (int i = 0; i < names.length; i++) {
            Circle circle = new Circle(RADIUS_CIRCLE, Color.TRANSPARENT);
            circle.setStroke(Color.BLACK);
            Label label = new Label(names[i]);
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(1.8*RADIUS_CIRCLE);
            if (names[i].equals("Start")) circle.setStroke(Color.GREEN);
            if (names[i].equals("End")) circle.setStroke(Color.RED);
            root.getChildren().addAll(circle,label);
            nodes.add(circle);
            Point2D current = center.add(RADIUS_FROM_CENTER*Math.sin(-angle),RADIUS_FROM_CENTER*Math.cos(-angle));
            angle+=difAngle;
            AnchorPane.positionInArea(circle, current.getX()-RADIUS_CIRCLE,
                    current.getY()-RADIUS_CIRCLE,
                    RADIUS_CIRCLE * 2, RADIUS_CIRCLE * 2,
                    0, null, HPos.CENTER, VPos.CENTER, false);
            AnchorPane.positionInArea(label,current.getX()-2*RADIUS_CIRCLE+10,current.getY()-10,2*RADIUS_CIRCLE-10,label.getHeight(),0,null,HPos.CENTER,VPos.CENTER,false);
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j].length()>0){
                    LineArrow line = link(nodes.get(i),nodes.get(j));
                    Text text = new Text(matrix[i][j]);
                    double xVector = line.getEndX()-line.getStartX();
                    double yVector = line.getEndY()-line.getStartY();
                    text.setRotate(Math.atan2(yVector,xVector)*180/Math.PI);
                    text.setLayoutX(line.getStartX()+xVector/4);
                    text.setLayoutY(line.getStartY()+yVector/4);
                    root.getChildren().addAll(line,line.getLine1(),line.getLine2(),text);
                }
            }
        }

        Scene scene = new Scene(root,800,600);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private LineArrow link(Circle circle1, Circle circle2){
        double x1 = circle1.getLayoutX();
        double y1 = circle1.getLayoutY();
        double x2 = circle2.getLayoutX();
        double y2 = circle2.getLayoutY();
        double xVector = x2-x1;
        double yVector = y2-y1;
        double lenghtVector = Math.sqrt(xVector*xVector+yVector*yVector);

        double x1Radius = xVector/(lenghtVector)*RADIUS_CIRCLE+x1;
        double y1Radius = yVector/(lenghtVector)*RADIUS_CIRCLE+y1;

        double lenghtVector2 =lenghtVector- 2*RADIUS_CIRCLE;

        double x2Radius = x1Radius+xVector/(lenghtVector)*lenghtVector2;
        double y2Radius = y1Radius+yVector/(lenghtVector)*lenghtVector2;
        LineArrow line = new LineArrow();
        line.setStartX(x1Radius);
        line.setStartY(y1Radius);
        line.setEndX(x2Radius);
        line.setEndY(y2Radius);
        line.setArrowCoordinates();

        return line;
    }
}
