package imageManipulation;

import basic.Point;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PixelView extends HBox {

	private SimpleObjectProperty<PixelInfo> pixelProperty;
	
	public PixelView(PixelInfo pixelInfo) {
		pixelProperty.set(pixelInfo);
		
		Rectangle bigPixel = new Rectangle(16, 16, pixelInfo.color);
		
		HBox redBox = new HBox();
		Rectangle redAmount = new Rectangle(4, 8*pixelInfo.color.getRed(), Color.RED);
		redBox.getChildren().add(redAmount);
		redBox.getChildren().add(new Label("Red"));
		
		HBox greenBox = new HBox();
		Rectangle greenAmount = new Rectangle(4, 8*pixelInfo.color.getGreen(), Color.GREEN);
		greenBox.getChildren().add(greenAmount);
		greenBox.getChildren().add(new Label("Green"));
		
		HBox blueBox = new HBox();
		Rectangle blueAmount = new Rectangle(4, 8*pixelInfo.color.getBlue());
		blueBox.getChildren().add(blueAmount);
		blueBox.getChildren().add(new Label("Blue"));
		
		VBox infoBox = new VBox(redBox, greenBox, blueBox);
		
		this.getChildren().addAll(bigPixel, infoBox);
	}
	
}

class PixelInfo {
	
	public Color color;
	public Point location;
	
	public PixelInfo(Color color, Point location) {
		this.color = color;
		this.location = location;
	}
	
	public int getARGB() {
		int alpha = (int) color.getOpacity()*255;
		int red = (int) color.getRed()*255;
		int green = (int) color.getGreen()*255;
		int blue = (int) color.getBlue()*255;		
		
		return (alpha<<24) + (red<<16) + (green<<8) + blue;
	}
	
	public double[] getHSB() {
		double hue = color.getHue();
		double saturation = color.getSaturation();
		double brightness = color.getBrightness();
		
		return new double[]{hue, saturation, brightness};
	}
	
}
