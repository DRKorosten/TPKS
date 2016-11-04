import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.tools.internal.ws.processor.model.Model;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RootLayout extends AnchorPane {
    //Local variables
    private boolean isFirstTarget = true;
    public ArrayList<ObjectModel> models = new ArrayList<>();
//    public ArrayList<ObjectModel> OBmodels = new ArrayList<>();
    @FXML
    SplitPane base_pane;
    @FXML
    AnchorPane right_pane;
    @FXML
    VBox left_pane;
    private DragIcon mDragOverIcon;
    private LineArrow line;
    private EventHandler<DragEvent> mIconDragOverRoot;
    private EventHandler<DragEvent> mIconDragDropped;
    private EventHandler<DragEvent> mIconDragOverRightPane;


    public RootLayout() {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("resources/RootLayout.fxml")
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
    private void initialize() {

        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root anchorpane so it can be visible
        //on both sides of the split pane.
        mDragOverIcon = new DragIcon();
        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);

        //create all items on left pane
        for (int i = 0; i < 4; i++) {
            DragIcon icn = new DragIcon();
            addDragDetection(icn);
            icn.setType(DragIconType.values()[i]);
            left_pane.getChildren().add(icn);
        }
        buildDragHandlers();
    }

    public void addModel(ObjectModel model) {
        models.add(model);
    }

    public void removeModel(ObjectModel model) {
        models.remove(model);
    }

    public void createNODE(ObjectModel model) {
        DraggableNode node = new DraggableNode(this, model);
        right_pane.getChildren().add(node);
    }


    public void drawLineArrow(Circle circle) {
        if (!circle.equals(((DraggableNode)circle.getParent()).top)){
            switch (((DraggableNode)circle.getParent()).getType()){
                case start:
                    if ((((DraggableNode)circle.getParent()).model.out[0])!=(null)){
                      return;
                    }
                    break;
                case rectangle:
                    if ((((DraggableNode)circle.getParent()).model.out[0])!=(null)){
                        return;
                    }
                    break;
                case rhomb:
                    if (((DraggableNode)circle.getParent()).getType(circle).equals(LinkSideType.left)&&(((DraggableNode)circle.getParent()).model.out[0])!=(null)){
                        return;
                    }
                    if (((DraggableNode)circle.getParent()).getType(circle).equals(LinkSideType.right)&&(((DraggableNode)circle.getParent()).model.out[1])!=(null)){
                        return;
                    }
                    break;
            }
        }
            if (isFirstTarget) {
                if (circle.equals(((DraggableNode)circle.getParent()).top)) return;
                isFirstTarget = false;
                LineArrow tempLineArrow = new LineArrow();
                tempLineArrow.setStartX(circle.getCenterX()+circle.getParent().getLayoutX());
                tempLineArrow.setStartY(circle.getCenterY()+circle.getParent().getLayoutY());
                tempLineArrow.setEndX(circle.centerXProperty().add(circle.getParent().layoutXProperty()).doubleValue());
                tempLineArrow.setEndY(circle.centerYProperty().add(circle.getParent().layoutYProperty()).doubleValue());
                tempLineArrow.startXProperty().bind(circle.centerXProperty().add(circle.getParent().layoutXProperty()));
                tempLineArrow.startYProperty().bind(circle.centerYProperty().add(circle.getParent().layoutYProperty()));
                tempLineArrow.setStrokeWidth(2);
                right_pane.getChildren().addAll(tempLineArrow,tempLineArrow.getLine1(),tempLineArrow.getLine2());
                tempLineArrow.setArrowCoordinates();
                right_pane.setOnMouseMoved(event -> {
                    if (event.getX() > tempLineArrow.getStartX()) {
                        tempLineArrow.setEndX(event.getX() - 2);
                    } else {
                        tempLineArrow.setEndX(event.getX() + 2);
                    }
                    if (event.getY() > tempLineArrow.getStartY()) {
                        tempLineArrow.setEndY(event.getY() - 2);
                    } else {
                        tempLineArrow.setEndY(event.getY() + 2);
                    }
                    tempLineArrow.setArrowCoordinates();
                });
                setDeleteOnDoubleClick(tempLineArrow);
                right_pane.setOnMouseClicked(event -> {
                    if (!(event.getTarget() instanceof Circle)) {
                        right_pane.getChildren().removeAll(tempLineArrow,tempLineArrow.getLine1(),tempLineArrow.getLine2());
                        isFirstTarget = true;
                    }
                });
                line = tempLineArrow;
                this.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ESCAPE)) {
                        right_pane.getChildren().removeAll(line,line.getLine1(),line.getLine2());
                        isFirstTarget = true;
                    }
                });

            } else {
                if (getNodesFromLineArrow(line)[0].equals(circle.getParent())) return;
                right_pane.setOnMouseMoved(null);
                line.endXProperty().bind(circle.centerXProperty().add(circle.getParent().layoutXProperty()));
                line.endYProperty().bind(circle.centerYProperty().add(circle.getParent().layoutYProperty()));
                wireLink(line);
                right_pane.setOnMouseClicked(null);
                line.setArrowCoordinates();
                line.linkArrow();
                isFirstTarget = true;


            }
    }


    private void addDragDetection(DragIcon dragIcon) {

        dragIcon.setOnDragDetected(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                // set drag event handlers on their respective objects
                base_pane.setOnDragOver(mIconDragOverRoot);
                right_pane.setOnDragOver(mIconDragOverRightPane);
                right_pane.setOnDragDropped(mIconDragDropped);

                // get a reference to the clicked DragIcon object
                DragIcon icn = (DragIcon) event.getSource();

                //begin drag ops
                mDragOverIcon.setType(icn.getType());
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);

                mDragOverIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);
                event.consume();
            }
        });
    }

    private void buildDragHandlers() {

        //drag over transition to move widget form left pane to right pane
        mIconDragOverRoot = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

                //turn on transfer mode and track in the right-pane's context
                //if (and only if) the mouse cursor falls within the right pane's bounds.
                if (!right_pane.boundsInLocalProperty().get().contains(p)) {

                    event.acceptTransferModes(TransferMode.ANY);
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mIconDragOverRightPane = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                //convert the mouse coordinates to scene coordinates,
                //then convert back to coordinates that are relative to
                //the parent of mDragIcon.  Since mDragIcon is a child of the root
                //pane, coodinates must be in the root pane's coordinate system to work
                //properly.
                mDragOverIcon.relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                event.consume();
            }
        };

        mIconDragDropped = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                container.addData("scene_coords",
                        new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };

        RootLayout t = this;

        this.setOnDragDone(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
                right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

                mDragOverIcon.setVisible(false);

                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                if (container != null) {
                    if (container.getValue("scene_coords") != null) {
                        if (!DragIconType.valueOf(container.getValue("type")).equals(DragIconType.start) && !DragIconType.valueOf(container.getValue("type")).equals(DragIconType.end)) {
                            DraggableNode node = new DraggableNode(t);

                            node.setType(DragIconType.valueOf(container.getValue("type")));
                            right_pane.getChildren().add(node);

                            Point2D cursorPoint = container.getValue("scene_coords");

                            node.relocateToPoint(
                                    new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                            );
                        } else {
                            for (Node node : right_pane.getChildren()
                                    ) {
                                if (node instanceof DraggableNode)
                                    if (((DraggableNode) (node)).getType().equals(DragIconType.valueOf(container.getValue("type")))) {
                                        return;
                                    }
                            }
                            DraggableNode node = new DraggableNode(t);

                            node.setType(DragIconType.valueOf(container.getValue("type")));
                            right_pane.getChildren().add(node);

                            Point2D cursorPoint = container.getValue("scene_coords");

                            node.relocateToPoint(
                                    new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                            );
                        }
                    }
                }

                container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.DragNode);

                if (container != null) {
                    if (container.getValue("type") != null)
                        System.out.println("Moved node " + container.getValue("type"));
                }

                event.consume();
            }
        });
    }

    public void showAfterLoad(ArrayList<ObjectModel> dataModels) {
        clearRP();
        for (int i = 0; i < dataModels.size(); i++) {
            createNODE(dataModels.get(i));
        }
    }

    public void clearRP() {

        if (!right_pane.isDisable()){
            right_pane.getChildren().clear();
            models.clear();
        }
    }

    public double[][] getLines() {
        int k = 0;
        for (Node node : right_pane.getChildren()) {
            if (node instanceof LineArrow) {
                k++;
            }
        }
        double[][] lines = new double[k][4];
        k = 0;
        for (Node node : right_pane.getChildren()) {
            if (node instanceof LineArrow) {
                lines[k][0] = ((LineArrow) node).getStartX();
                lines[k][1] = ((LineArrow) node).getStartY();
                lines[k][2] = ((LineArrow) node).getEndX();
                lines[k][3] = ((LineArrow) node).getEndY();
                k++;
            }
        }
        return lines;
    }

    public void createLines(double[][] linesCoordinates) {
        for (int i = 0; i < linesCoordinates.length; i++) {
            LineArrow line = new LineArrow();
            line.setStrokeWidth(2);
            right_pane.getChildren().addAll(line,line.getLine1(),line.getLine2());
            for (Node node : right_pane.getChildren()) {
                if (node instanceof DraggableNode) {
                    for (Node cNode : ((DraggableNode) node).getChildren()) {
                        if (cNode instanceof Circle) {
                            if (Math.abs(((Circle) cNode).getCenterX() + cNode.getParent().getLayoutX() - linesCoordinates[i][0]) < 3
                                    && Math.abs(((Circle) cNode).getCenterY() + cNode.getParent().getLayoutY() - linesCoordinates[i][1]) < 3) {
                                line.startXProperty().bind(((Circle) cNode).centerXProperty().add(node.layoutXProperty()));
                                line.startYProperty().bind(((Circle) cNode).centerYProperty().add(node.layoutYProperty()));

                            } else {
                                if (Math.abs(((Circle) cNode).getCenterX() + cNode.getParent().getLayoutX() - linesCoordinates[i][2]) < 3
                                        && Math.abs(((Circle) cNode).getCenterY() + cNode.getParent().getLayoutY() - linesCoordinates[i][3]) < 3) {
                                    line.endXProperty().bind(((Circle) cNode).centerXProperty().add(node.layoutXProperty()));
                                    line.endYProperty().bind(((Circle) cNode).centerYProperty().add(node.layoutYProperty()));
                                }
                            }
                        }
                    }
                }
            }
           setDeleteOnDoubleClick(line);

           line.setArrowCoordinates();
           line.linkArrow();
        }

    }

    private void setDeleteOnDoubleClick(LineArrow line ){
        line.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
              deleteLink(line);
                event.consume();
            }
        });
    }

    void deleteLink(LineArrow line){
    DraggableNode[] nodes = getNodesFromLineArrow(line);

    switch (nodes[0].mType){
        case start:
            nodes[0].model.removeOut(true);
            break;
        case rectangle:
            switch (nodes[0].whatCircleContain(line.getStartX(),line.getStartY())){
                case top:
                    nodes[0].model.removeEntry(nodes[1].model);
                    break;
                case bottom:
                    nodes[0].model.removeOut(true);
                    break;
            }
            break;
        case rhomb:
            switch (nodes[0].whatCircleContain(line.getStartX(),line.getStartY())){
                case top:
                    nodes[0].model.removeEntry(nodes[1].model);
                    break;
                case left:
                    nodes[0].model.removeOut(true);
                    break;
                case right:
                    nodes[0].model.removeOut(false);
                    break;
            }
            break;
        case end:
            nodes[0].model.removeEntry(nodes[1].model);
            break;
    }
    switch (nodes[1].mType){
        case start:
            nodes[1].model.removeOut(true);
            break;
        case rectangle:
            switch (nodes[1].whatCircleContain(line.getEndX(),line.getEndY())){
                case top:
                    nodes[1].model.removeEntry(nodes[0].model);
                    break;
                case bottom:
                    nodes[1].model.removeOut(true);
                    break;
            }
            break;
        case rhomb:
            switch (nodes[1].whatCircleContain(line.getEndX(),line.getEndY())){
                case top:
                    nodes[1].model.removeEntry(nodes[0].model);
                    break;
                case left:
                    nodes[1].model.removeOut(true);
                    break;
                case right:
                    nodes[1].model.removeOut(false);
                    break;
            }
            break;
        case end:
            nodes[1].model.removeEntry(nodes[0].model);
            break;
    }

    right_pane.getChildren().removeAll(line,line.getLine1(),line.getLine2());
}

    void wireLink(LineArrow line){
        DraggableNode[] nodes = getNodesFromLineArrow(line);
        switch (nodes[0].mType){
            case start:
                nodes[0].model.addOut(nodes[1].model,true);
                break;
            case rectangle:
                switch (nodes[0].whatCircleContain(line.getStartX(),line.getStartY())){
                    case top:
                        nodes[0].model.addEntry(nodes[1].model);
                        break;
                    case bottom:
                        nodes[0].model.addOut(nodes[1].model,true);
                        break;
                }
                break;
            case rhomb:
                switch (nodes[0].whatCircleContain(line.getStartX(),line.getStartY())){
                    case top:
                        nodes[0].model.addEntry(nodes[1].model);
                        break;
                    case left:
                        nodes[0].model.addOut(nodes[1].model,true);
                        break;
                    case right:
                        nodes[0].model.addOut(nodes[1].model,false);
                        break;
                }
                break;
            case end:
                nodes[0].model.addEntry(nodes[1].model);
                break;
        }
        switch (nodes[1].mType){
            case start:
                nodes[1].model.addOut(nodes[0].model,true);
                break;
            case rectangle:
                switch (nodes[1].whatCircleContain(line.getEndX(),line.getEndY())){
                    case top:
                        nodes[1].model.addEntry(nodes[0].model);
                        break;
                    case bottom:
                        nodes[1].model.addOut(nodes[0].model,true);
                        break;
                }
                break;
            case rhomb:
                switch (nodes[1].whatCircleContain(line.getEndX(),line.getEndY())){
                    case top:
                        nodes[1].model.addEntry(nodes[0].model);
                        break;
                    case left:
                        nodes[1].model.addOut(nodes[0].model,true);
                        break;
                    case right:
                        nodes[1].model.addOut(nodes[0].model,false);
                        break;
                }
                break;
            case end:
                nodes[1].model.addEntry(nodes[0].model);
                break;
        }
        for (DraggableNode dn:nodes
                ) {
            System.out.println(dn.model);
        }
    }

    /**
     * return array of two() draggable nodes that connected by link
     * @param l
     * @return
     */
    private DraggableNode[] getNodesFromLineArrow(LineArrow l){
        DraggableNode[] res = new DraggableNode[2];

        for(Node node: right_pane.getChildren()){
            if(node instanceof DraggableNode){
                if(((DraggableNode)node).con(l.getStartX(),l.getStartY())){
                    res[0] = (DraggableNode) node;
                }else if(((DraggableNode)node).con(l.getEndX(),l.getEndY())) {
                    res[1] = (DraggableNode) node;
                }
            }

        }
        return res;
    }
    /*
    return true if all is fine
     */
    public boolean checkAllSystem(){
        Alert alert = new Alert(Alert.AlertType.NONE);
        boolean isStart= false, isEnd = false,isFloatingNodes = true;
        int counterFnodes = 0;
        for (Node node :
                right_pane.getChildren()) {
            if (node instanceof DraggableNode){
                if (((DraggableNode) node).getType().equals(DragIconType.start)) isStart = true;
                if (((DraggableNode) node).getType().equals(DragIconType.end)) isEnd = true;
                if (!isNoFloatingNode(((DraggableNode) node).model)){
                    isFloatingNodes = false;
                    counterFnodes++;
                }
            }
        }
        if (isStart&&isEnd&&isFloatingNodes) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Your system is doing great.No errors.");
        }else {
            String contentText = "";
            if (!isStart) contentText+=("Node Start is missing. ");
            if (!isEnd) contentText = contentText +("Node End is missing. ");
            if (!isFloatingNodes) contentText+=("There is "+ counterFnodes+ " floating nodes");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(contentText);
        }
        alert.showAndWait();
       return !alert.getAlertType().equals(Alert.AlertType.ERROR);
    }
    private boolean isNoFloatingNode(ObjectModel node){
        boolean flag = true;
        if (node.entry.size()==0 && !node.type.equals(DragIconType.start)){
            flag = false;
            return flag;
        }
        if (!node.type.equals(DragIconType.end)) {
            for (ObjectModel n :
                    node.out) {
                if (n == null) {
                    flag = false;
                    return flag;
                }
            }
        }
        return flag;
    }
     int [][] getRouteMatrix() throws Exception {
        int[][] matrix;
        if (checkAllSystem()) {
//            int k= 0 ;
//            for (Node node  :right_pane.getChildren()) {
//                if (node instanceof DraggableNode && !((DraggableNode) node).getType().equals(DragIconType.rhomb)){
//                    k++;
//                }
//            }
            matrix = new int[models.size()][models.size()];
            for (ObjectModel m : models) {
                for (int i = 0; i < m.out.length; i++) {
                    //1-true
                    //2-false
                    //3-both outs in same node
                    matrix[models.indexOf(m)][models.indexOf(m.out[i])]+=(i==0)? 1:2;
                }
            }
            return matrix;
        }
        throw new Exception("System failed");
    }
    String[] getLabels(){
        ArrayList<String> result = new ArrayList<>();
        for (Node node  :right_pane.getChildren()) {
            if (node instanceof DraggableNode
//                    && !((DraggableNode) node).getType().equals(DragIconType.rhomb)
                    ){
                result.add(new String(((DraggableNode) node).getText()));
            }
        }
        return result.toArray(new String[result.size()]);
    }
    String [][] getOPBlockMatrix() {
        ObjectModel model;
        int k = 0;
        for (ObjectModel m: models){
          if (m.type.equals(DragIconType.start)){
              model = m;
          }
          if (!m.type.equals(DragIconType.rhomb)){
              k++;
          }
        }
        String[][] result = new String[k][k];

      return result;
    }


}
