
public class ObjectModel {
    DragIconType type;
    LinkSideType entry ;
    LinkSideType[] out;

    public ObjectModel(DragIconType type){
        this.type = type;
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

    public boolean addEntry(String string){
        if (entry.equals(LinkSideType.none)){
            entry = LinkSideType.valueOf(string);
            return true;
        }else {
            return false;
        }
    }
    public void removeEntry(){
        entry = LinkSideType.none;
    }
    public boolean addOut(String string,boolean bool) {
        if (bool) {
            if (out[0].equals(LinkSideType.none)) {
                out[0] = LinkSideType.valueOf(string);
                return true;
            }

        } else {
            if (out.length == 2) {
                if (out[1].equals(LinkSideType.none)) {
                    out[1] = LinkSideType.valueOf(string);
                    return true;
                }
            }
        }
        return false;
    }
    public void removeOut(boolean bool){
        if (bool){
            out[0] = LinkSideType.none;
        }else {
            out[1]=LinkSideType.none;
        }
    }
}
