import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.*;

public class Main extends Application {
	RootLayout layout;
	boolean debugMode = true;

	void save(File f)  {
		try {
			FileWriter fstream1 = new FileWriter(f);// конструктор с одним параметром - для перезаписи
			BufferedWriter out1 = new BufferedWriter(fstream1); //  создаём буферезированный поток
			out1.write(""); // очищаем, перезаписав поверх пустую строку
			out1.close();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(new FlowChartSerializable(layout.models,layout.getLines()));
			oos.flush();
			oos.close();
		} catch (IOException e) {
			System.out.println("error save file " + f +" ");e.printStackTrace();
		}


	}

	void open(File f){
		try{
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fis);
			FlowChartSerializable data = (FlowChartSerializable) oin.readObject();
			layout.showAfterLoad(data.models);
			layout.models.clear();
			layout.models.addAll(data.models);
			layout.createLines(data.linesCoordinates);
			fis.close();
			oin.close();

		} catch (IOException e){
			System.out.println("error open file " + f);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}

	private File getFiles(String s,Stage primaryStage) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop")
		);
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All files", "*.*"),
				new FileChooser.ExtensionFilter("TPKS", "*.tpks")
		);
		if(s == "save") {
			return fileChooser.showSaveDialog(primaryStage);
		}
		return fileChooser.showOpenDialog(primaryStage);

	}
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		try {

			Scene scene = new Scene(root,720,450);
			scene.getStylesheets().add(getClass().getResource("resources/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("MODELING");
			primaryStage.setResizable(true);

			MenuBar menuBar = new MenuBar();
			menuBar.setUseSystemMenuBar(true);
			menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
			root.setTop(menuBar);

			// File menu - new, save, exit
			Menu fileMenu = new Menu("File");
			MenuItem newMenuItem = new MenuItem("New");
			MenuItem openMenuItem = new MenuItem("Open");
			MenuItem saveMenuItem = new MenuItem("Save");
			MenuItem exitMenuItem = new MenuItem("Exit");
			exitMenuItem.setOnAction(actionEvent -> Platform.exit());

			// Check menu - highlightUnconnected
			Menu checkMenu = new Menu("Check");
			MenuItem highlightUnconnected = new MenuItem("Подсветить висячие");
			highlightUnconnected.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN));
			checkMenu.getItems().add(highlightUnconnected);

			MenuItem makeGraph = new MenuItem("Построить граф");
			makeGraph.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN));
			checkMenu.getItems().add(makeGraph);

			openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
			saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
			newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

			newMenuItem.setOnAction(actionEvent ->{
				layout.clearRP();
				layout.models.clear();
			});
			makeGraph.setOnAction(actionEvent ->{
				try {
					int[][] ma = layout.getRouteMatrix();
					String [] strings = layout.getLabels();
					Graph graph = new Graph(ma,strings);
					graph.start(new Stage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});


			openMenuItem.setOnAction(actionEvent ->{
				File file = getFiles("open",new Stage());
				layout.models.clear();
				if (file!=null)
					open(file);
			});

			saveMenuItem.setOnAction(actionEvent ->{
				File file = getFiles("save",new Stage());
				if (file!=null)
					save(file);
			});

			highlightUnconnected.setOnAction(event -> {
				if (debugMode){
					layout.right_pane.setDisable(true);
					layout.right_pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45)");
					primaryStage.setTitle("DEBUG MODE");
//					layout.checkAllSystem();
					try {
						int[][] result = layout.getRouteMatrix();
						for (int i = 0; i < result.length; i++) {
							for (int j = 0; j < result[i].length; j++) {
								System.out.print(result[i][j]+" ");
							}
							System.out.println();
						}
					} catch (Exception e) {
						System.out.println(e);
					}
					for (Node node:	layout.right_pane.getChildren() ) {
						if (node instanceof DraggableNode) {
							DraggableNode n = (DraggableNode) node;
							System.out.println(n.model);
							switch (n.mType) {
								case rectangle:
									if (!(n.model.entry.size() > 0 && n.model.out[0] != null)) {
										n.setEffect(new DropShadow(10, Color.RED));
									}
									break;
								case rhomb:
									if (!(n.model.entry.size() > 0 && n.model.out[0] != null && n.model.out[1] != null)) {
										n.setEffect(new DropShadow(10, Color.RED));
									}
									break;
								case start:
									if (!(n.model.out[0] != null)) {
										n.setEffect(new DropShadow(10, Color.RED));
									}
									break;
								case end:
									if (!(n.model.entry.size() > 0)) {
										n.setEffect(new DropShadow(10, Color.RED));
									}
									break;
							}
						}}
				}else{
					layout.right_pane.setDisable(false);
					layout.right_pane.setStyle("-fx-background-color: transparent");
					primaryStage.setTitle("MODELING");
					for (Node node:	layout.right_pane.getChildren() ) {
						if (node instanceof DraggableNode) {
							node.setEffect(null);
						}
					}
				}
				debugMode = !debugMode;
			});


			fileMenu.getItems().addAll(newMenuItem, openMenuItem,saveMenuItem,
					new SeparatorMenuItem(), exitMenuItem);
			Menu help = new Menu("Help");
			MenuItem about = new MenuItem("About");
			about.setOnAction(actionEvent -> {

				BorderPane p = new BorderPane();
				p.setCenter(new Label("Авторы: Райзберг Д. Середюк М."));

				Stage stage = new Stage();
				stage.setTitle("About");
				stage.setScene(new Scene(p, 400, 150));
				stage.show();
			});

			help.getItems().addAll(about);

			menuBar.getMenus().addAll(fileMenu, checkMenu,help);


		} catch(Exception e) {
			System.out.println("error with menu");
		}
		layout = new RootLayout();
		root.setCenter(layout);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
