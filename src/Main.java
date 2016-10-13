
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {
RootLayout layout;

	void save(File f)  {
		if(f.equals(null)) return;
		try {
			FileWriter fstream1 = new FileWriter(f);// конструктор с одним параметром - для перезаписи
			BufferedWriter out1 = new BufferedWriter(fstream1); //  создаём буферезированный поток
			out1.write(""); // очищаем, перезаписав поверх пустую строку
			out1.close();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(new Data(layout.models,layout.getLines()));
			oos.flush();
			oos.close();
		} catch (IOException e) {
			System.out.println("error save file " + f +" ");e.printStackTrace();
		}


	}

	void open(File f){
		if(f.equals(null)) return;
		try{
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fis);
			Data data = (Data) oin.readObject();
			layout.models = data.models;
			layout.showAfterLoad();
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
		if(s == "save") return fileChooser.showSaveDialog(primaryStage);
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

			openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
			saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
			newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

			newMenuItem.setOnAction(actionEvent ->{
				layout.clearRP();

			});


			openMenuItem.setOnAction(actionEvent ->{
				File file = getFiles("open",primaryStage);
				open(file);

			});

			saveMenuItem.setOnAction(actionEvent ->{
				File file = getFiles("save",primaryStage);
				save(file);

			});


			fileMenu.getItems().addAll(newMenuItem, openMenuItem,saveMenuItem,
					new SeparatorMenuItem(), exitMenuItem);
			Menu help = new Menu("Help");
			MenuItem about = new MenuItem("About");
			about.setOnAction(actionEvent -> {

				BorderPane p = new BorderPane();
				p.setCenter(new Label("Автор: Райзберг Д. Середюк М."));

				Stage stage = new Stage();
				stage.setTitle("About");
				stage.setScene(new Scene(p, 400, 150));
				stage.show();
			});

			help.getItems().addAll(about);

			menuBar.getMenus().addAll(fileMenu, help);


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
