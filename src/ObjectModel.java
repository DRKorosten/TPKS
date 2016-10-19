import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectModel implements Serializable{
    DragIconType type;
    String text = "";
    double layoutX;
    double layoutY;
    ArrayList<DraggableNode> entry = new ArrayList<DraggableNode>();
    DraggableNode[] out = new DraggableNode[0];

//    private ObjectModel(){}
    public ObjectModel(DragIconType type,Point2D layout){
        this.type = type;
        layoutX = layout.getX();
        layoutY = layout.getY();
        switch (type){
            case rectangle:
                out = new DraggableNode[1];

                break;
            case rhomb:
                out = new DraggableNode[2];

                break;
            case start:
                out = new DraggableNode[1];
                break;
            case end:

                break;
        }
    }

    public void setText(String text){
        this.text = text;
    }

    public void setLayout(Point2D point){
        layoutX = point.getX();
        layoutY = point.getY();
    }
    public boolean addEntry(DraggableNode node){
        entry.add(node);

            return true;
    }
    public void removeEntry(DraggableNode node){
        entry.remove(entry.indexOf(node));
    }
    public void addOut(DraggableNode node,boolean bool) {
        if(out.length==1) {
            out[0] = node;
            return;
        }
        if (bool) {
                out[0] = node;

        } else {
            out[1] = node;
        }
    }
    public void removeOut(boolean bool){
        if(out.length==1) out[0] = null;
        if (bool) {
            out[0] = null;

        } else {
            out[1] = null;
        }
    }

    @Override
    public String toString() {
        String res= type.toString() +" "+ (layoutX) +" "+(layoutY)+" "+ " "+ Arrays.toString(out)+ " ";
        if(entry!=null){
            res+=entry.toString();
        }
        return res;

    }
}
