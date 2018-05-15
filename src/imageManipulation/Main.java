package imageManipulation;

import java.io.File;

import basic.BaseKit;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	private ImageManipulator manipulator = new ImageManipulator();
	
	@Override
	public void start(Stage stage) throws Exception {
		
		BorderPane rootBP = new BorderPane();
		rootBP.setPrefSize(1200, 512);
		rootBP.setMaxSize(1840, 1000);
		ScrollPane center = new ScrollPane();
		rootBP.setCenter(center);
		
//		center.setContent(new PixelGridView(new Image("File:ressources/bw.jpg"), 16));
		
		ImageView originalIV = new ImageView(new Image("File:ressources/phoenix.jpg"));
		ImageView prevIV = new ImageView(originalIV.getImage());
		ImageView currentIV = new ImageView(originalIV.getImage());
		HBox imageTrey = new HBox(8, originalIV, prevIV, currentIV);
		center.setContent(imageTrey);
		
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
				originalIV.setImage(new Image("File:" + selectedFile.getPath()));
				prevIV.setImage(null);
				currentIV.setImage(originalIV.getImage());
			} else
				System.out.println("Invalid file format");
		});
		buttonTray.getChildren().add(loadImg);
		
		ChoiceBox<Filter> filterChooser = new ChoiceBox<>();
		filterChooser.getItems().addAll(Filter.allFilters());
		filterChooser.getSelectionModel().selectFirst();
		
		Button applyFilter = new Button("apply");
		applyFilter.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.applyFilter(prev, filterChooser.getValue()));
		});
		
		HBox filtering = new HBox(4, filterChooser, applyFilter);
		buttonTray.getChildren().add(filtering);
		
		ChoiceBox<Integer> factorChooser = new ChoiceBox<>();
		factorChooser.getItems().addAll(2, 5, 10);
		factorChooser.getSelectionModel().selectFirst();
		
		SimpleDoubleProperty scalingFactor = new SimpleDoubleProperty(2);
		TextField scalingFactorField = new TextField("2");
		scalingFactorField.setPrefWidth(48);
		scalingFactorField.setOnAction((e)-> {
			try {
				scalingFactor.set(Double.parseDouble(scalingFactorField.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("No valid scaling factor");
				scalingFactorField.setText("");
			}
		});
		
		ChoiceBox<ScalingType> sclaingTypeChooser = new ChoiceBox<>();
		sclaingTypeChooser.getItems().addAll(ScalingType.values());
		sclaingTypeChooser.setPadding(new Insets(0, 0, 0, 4));
		sclaingTypeChooser.getSelectionModel().selectFirst();
		
		Button upscale = new Button("+");
		upscale.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.scale(prev, scalingFactor.get(), sclaingTypeChooser.getValue()));
		});
		
		Button downscale = new Button("-");
		downscale.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.scale(prev, 1.0/scalingFactor.get(), sclaingTypeChooser.getValue()));
		});
		
		HBox sclaing = new HBox(4, downscale, scalingFactorField, upscale, sclaingTypeChooser);
		buttonTray.getChildren().add(sclaing);
		
		Button invert = new Button("invert");
		invert.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.pixelwiseOp(prev, PixelOp.INVERT));
		});
		
		Button greyScale = new Button("grey scale");
		greyScale.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.pixelwiseOp(prev, PixelOp.GREY_SCALE));
		});
		
		Button colorNormalization = new Button("C-Norm");
		colorNormalization.setOnAction((e)->{
			Image prev = currentIV.getImage();
			if(prev == null)
				prev = originalIV.getImage();
			prevIV.setImage(currentIV.getImage());
			currentIV.setImage(manipulator.colorNormalization(prev));
		});
		
		HBox pixelOps = new HBox(4, invert, greyScale, colorNormalization);
		buttonTray.getChildren().add(pixelOps);
		
		ChoiceBox<ToolCode> toolSelector = new ChoiceBox<>();
		toolSelector.getItems().addAll(ToolCode.values());
		toolSelector.setPadding(new Insets(0, 0, 0, 4));
		toolSelector.getSelectionModel().selectFirst();
		
		ColorPicker colorSelector = new ColorPicker(Color.WHITE);
		
		SimpleDoubleProperty fillToleranz = new SimpleDoubleProperty(2);
		TextField fillToleranzSelector = new TextField("0");
		fillToleranzSelector.setPrefWidth(48);
		fillToleranzSelector.setOnAction((e)-> {
			try {
				fillToleranz.set(Double.parseDouble(fillToleranzSelector.getText()));
			} catch (NumberFormatException nfe) {
				System.out.println("No valid toleranz");
				fillToleranzSelector.setText("");
			}
		});
		
		ToggleButton square = new ToggleButton("square");
		
		currentIV.setOnMousePressed((e)-> {
			switch (toolSelector.getValue()) {
			case FLOOD_FILL:
				Image prev = currentIV.getImage();
				if(prev == null)
					prev = originalIV.getImage();
				prevIV.setImage(currentIV.getImage());
				currentIV.setImage(manipulator.floodFill(prev, (int) e.getX(), (int) e.getY(), colorSelector.getValue(), fillToleranz.get(), square.isPressed()));
				break;
			default:
				System.err.println("Invalid ToolCode");
				break;
			}
		});
		
		HBox toolBox = new HBox(4, toolSelector, colorSelector, square, fillToleranzSelector);
		buttonTray.getChildren().add(toolBox);
		
		Button undo = new Button("undo");
		undo.setOnAction((e)-> {
			currentIV.setImage(prevIV.getImage());
		});
		
		Button restore = new Button("restore");
		restore.setOnAction((e)-> {
			currentIV.setImage(originalIV.getImage());
		});
		
		HBox metaOps = new HBox(4, undo, restore);
		buttonTray.getChildren().add(metaOps);
		
		Scene scene = new Scene(rootBP);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Main.launch(args);
	}
	
}