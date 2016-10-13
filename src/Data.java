import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
    ArrayList<ObjectModel> models;
    double[][] linesCoordinates;

    public Data(ArrayList<ObjectModel> models, double[][] links) {
        this.models = models;
        this.linesCoordinates = links;
    }
}
