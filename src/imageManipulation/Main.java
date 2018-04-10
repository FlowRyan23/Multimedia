package imageManipulation;

import java.io.File;

import basic.BaseKit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
		
		Button applyFilter = new Button("apply");
		applyFilter.setOnAction((e)->{
			ImageView centerIV = (ImageView) (center.getContent());
			centerIV.setImage(manipulator.applyFilter(centerIV.getImage(), filterChooser.getValue()));
		});
		
		HBox filtering = new HBox(4, filterChooser, applyFilter);
		buttonTray.getChildren().add(filtering);
		
		ChoiceBox<Integer> factorChooser = new ChoiceBox<>();
		factorChooser.getItems().addAll(1, 2, 5, 10);
		
		ChoiceBox<ScalingType> sclaingTypeChooser = new ChoiceBox<>();
		sclaingTypeChooser.getItems().addAll(ScalingType.values());
		sclaingTypeChooser.setPadding(new Insets(0, 0, 0, 4));
		
		Button upscale = new Button("+");
		upscale.setOnAction((e)->{
			ImageView centerIV = (ImageView) (center.getContent());
			centerIV.setImage(manipulator.scale(centerIV.getImage(), factorChooser.getValue(), sclaingTypeChooser.getValue()));
		});
		
		Button downscale = new Button("-");
		downscale.setOnAction((e)->{
			ImageView centerIV = (ImageView) (center.getContent());
			centerIV.setImage(manipulator.scale(centerIV.getImage(), 1.0/factorChooser.getValue(), sclaingTypeChooser.getValue()));
		});
		
		HBox sclaing = new HBox(4, downscale, factorChooser, upscale, sclaingTypeChooser);
		buttonTray.getChildren().add(sclaing);
		
		Scene scene = new Scene(rootBP);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Main.launch(args);
	}
	
}