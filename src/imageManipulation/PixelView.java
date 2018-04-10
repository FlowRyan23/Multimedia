package imageManipulation;

import basic.Point;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PixelView extends HBox {

	private SimpleObjectProperty<PixelInfo> pixelProperty = new SimpleObjectProperty<>();
	
	public PixelView(PixelInfo pixelInfo, int size) {
		pixelProperty.set(pixelInfo);
		
		Rectangle bigPixel = new Rectangle(size, size, pixelInfo.color);

		Rectangle redAmount = new Rectangle(4, size*pixelInfo.color.getRed(), Color.RED);
		Rectangle greenAmount = new Rectangle(4, size*pixelInfo.color.getGreen(), Color.GREEN);
		Rectangle blueAmount = new Rectangle(4, size*pixelInfo.color.getBlue(), Color.BLUE);
		
		this.setSpacing(1);
		this.getChildren().addAll(bigPixel, redAmount, greenAmount, blueAmount);
	}
	
}

class PixelGridView extends GridPane {
	
	public PixelGridView(Image img, int size) {
		System.out.println("w " + img.getWidth() + ", h " + img.getHeight());
		
		PixelReader reader = img.getPixelReader();
		
		for(int x=0; x<img.getWidth(); x++) {
			PixelView[] column = new PixelView[(int) img.getHeight()];
			for(int y=0; y<img.getHeight(); y++) {
				column[y] = new PixelView(new PixelInfo(reader.getColor(x, y), new Point(x, y)), size);
				column[y].setPadding(new Insets(1));
			}
			this.addColumn(x, column);
		}
				
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
