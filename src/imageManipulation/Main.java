package imageManipulation;

import java.io.File;

import basic.BaseKit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	private ImageManipulator manipulator = new ImageManipulator();
	
	@Override
	public void start(Stage stage) throws Exception {
		
		BorderPane rootBP = new BorderPane();
		rootBP.setPrefSize(512, 320);
		rootBP.setMaxSize(1840, 1000);
		ScrollPane center = new ScrollPane();
		rootBP.setCenter(center);
				
		VBox buttonTray = new VBox(16);
		rootBP.setRight(buttonTray);
		
		Button loadImg = new Button("Load Image");
		loadImg.setOnAction((e)->{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose Image");
			fileChooser.setInitialDirectory(new File("./ressources"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			if(BaseKit.isImageFile(selectedFile)) {
				System.out.println(selectedFile.getPath());
				center.setContent(new ImageView("File:" + selectedFile.getPath()));
			} else
				System.out.println("Invalid file format");
		});
		buttonTray.getChildren().add(loadImg);
		
		ChoiceBox<Filter> filterChooser = new ChoiceBox<>();
		filterChooser.getItems().addAll(Filter.allFilters());
		buttonTray.getChildren().add(filterChooser);
		
		Button applyFilter = new Button("apply");
		applyFilter.setOnAction((e)->{
			ImageView centerIV = (ImageView) (center.getContent());
			centerIV.setImage(manipulator.applyFilter(centerIV.getImage(), filterChooser.getValue()));
		});
		buttonTray.getChildren().add(applyFilter);
		
		Scene scene = new Scene(rootBP);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Main.launch(args);
	}
	
}