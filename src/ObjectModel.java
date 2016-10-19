import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectModel implements Serializable{
    DragIconType type;
    String text = "";
    double layoutX;
    double layoutY;
    ArrayList<DraggableNode> entry ;
    DraggableNode[] out;


    public ObjectModel(DragIconType type,Point2D layout){
        this.type = type;
        layoutX = layout.getX();
        layoutY = layout.getY();
        entry = null;
        if (type.equals(DragIconType.rectangle)) {
            out = new DraggableNode[1];
            out[0]=null;
        }else {
            out = new DraggableNode[2];
            out[0]=null;
            out[1]=null;
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
        if(out.length==1) out[0] = node;
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
        return type.toString() +" "+ (layoutX) +" "+(layoutY)+" "+ " "+ out[0]+" "+ (out.length > 1 ? out[1]: " ")+ " "+entry;
    }
}
