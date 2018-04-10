package imageManipulation;

import java.util.LinkedList;

import basic.BaseKit;
import basic.Point;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		
		BorderPane rootBP = new BorderPane();
		
		Image img = new Image("File:ressources/bw.jpg");
		PixelReader pixelReader = img.getPixelReader();
		
		GridPane pixelInfoRastar = new GridPane();
		for(int x=0; x<img.getWidth() ; x++) {
			LinkedList<Node> row = new LinkedList<>();
			for(int y=0; y<img.getHeight(); y++) {
				byte[] argbList = BaseKit.argbUnpack(pixelReader.getArgb(x, y));
				row.add(new PixelView(new PixelInfo(Color.rgb(argbList[1], argbList[2], argbList[3], argbList[0]), new Point(x, y))));
			}
			pixelInfoRastar.addRow(x, (Node[]) row.toArray());
		}
		
		rootBP.setCenter(pixelInfoRastar);
		
		Scene scene = new Scene(rootBP);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Main.launch(args);
	}
	
}