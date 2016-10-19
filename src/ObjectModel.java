import javafx.geometry.Point2D;

import java.io.Serializable;

public class ObjectModel implements Serializable{
    DragIconType type;
    String text = "";
    double layoutX;
    double layoutY;
    LinkSideType entry ;
    LinkSideType[] out;


    public ObjectModel(DragIconType type,Point2D layout){
        this.type = type;
        layoutX = layout.getX();
        layoutY = layout.getY();
        entry = LinkSideType.none;
        if (type.equals(DragIconType.rectangle)) {
            out = new LinkSideType[1];
            out[0]=LinkSideType.none;
        }else {
            out = new LinkSideType[2];
            out[0]=LinkSideType.none;
            out[1]=LinkSideType.none;
        }
    }

    public void setText(String text){
        this.text = text;
    }

    public void setLayout(Point2D point){
        layoutX = point.getX();
        layoutY = point.getY();
    }
    public boolean addEntry(LinkSideType type){
        if (entry.equals(LinkSideType.none)){
            entry = type;
            return true;
        }else {
            return false;
        }
    }
    public void removeEntry(){
        entry = LinkSideType.none;
    }
    public boolean addOut(LinkSideType type,boolean bool) {
        if (bool) {
            if (out[0].equals(LinkSideType.none)) {
                out[0] = type;
                return true;
            }

        } else {
            if (out.length == 2) {
                if (out[1].equals(LinkSideType.none)) {
                    out[1] = type;
                    return true;
                }
            }
        }
        return false;
    }
    public void removeOut(LinkSideType type, boolean bool){
        if (bool){
            out[0] = LinkSideType.none;
        }else {
            out[1]=LinkSideType.none;
        }
    }

    @Override
    public String toString() {
        return type.toString() +" "+ (layoutX) +" "+(layoutY)+" "+ " "+ out[0]+" "+ (out.length > 1 ? out[1]: " ")+ " "+entry;
    }
}
