import java.io.Serializable;

/**
 * Created by Dmytro on 02.11.16.
 */
public class GraphSerializable implements Serializable{
    int[][] matrix;
    String[] names;

    public GraphSerializable(int[][] matrix, String[] names) {
        this.matrix = matrix;
        this.names = names;
    }
}
