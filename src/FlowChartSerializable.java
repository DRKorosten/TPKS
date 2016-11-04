import java.io.Serializable;
import java.util.ArrayList;

public class FlowChartSerializable implements Serializable {
    ArrayList<ObjectModel> models;
    double[][] linesCoordinates;

    public FlowChartSerializable(ArrayList<ObjectModel> models, double[][] links) {
        this.models = models;
        this.linesCoordinates = links;
    }
}
