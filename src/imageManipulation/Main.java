package imageManipulation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

	private Manipulator manipulator = new Manipulator();
	
	@Override
	public void start(Stage stage) throws Exception {
		
		BorderPane rootBP = new BorderPane();				
		
		Image img = new Image("File:ressources/bw.jpg");
		ImageView originalIV = new ImageView(img);
		ImageView a = new ImageView(manipulator.applyFilter(img, Filter.meanBlur));
		
		HBox hbox = new HBox(8, originalIV, a);
		rootBP.setCenter(hbox);
		
		Scene scene = new Scene(rootBP);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Main.launch(args);
	}
	
}