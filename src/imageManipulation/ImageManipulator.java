package imageManipulation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageManipulator {
	
	public Image applyFilter(Image img, Filter filter) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		double[][] kernel = filter.kernel;
		double correction = filter.correction;
		WritableImage res = new WritableImage(width, height);
		
		PixelReader pixelReader = img.getPixelReader();
				
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {
				int red=0, green=0, blue=0;
				int gray=0;
				
				for(int w=0; w<kernel.length; w++)
					for(int h=0; h<kernel[w].length; h++) {
						int posX = x -kernel.length/2 +w;
						int posY = y -kernel[w].length/2 + h;
						
						if(posX < 0 || posX >= width || posY < 0 || posY >= height)
							continue;
						
						int argb = pixelReader.getArgb(posX, posY);
						red += kernel[w][h] * ((0xff<<16 & argb)>>>16);
						green += kernel[w][h] * ((0xff<<8 & argb)>>>8);
						blue += kernel[w][h] * (0xff & argb);
						
						gray += kernel[w][h] * pixelReader.getColor(posX, posY).grayscale().getBrightness() * 255;					
					}
				
				if(filter.color) {
					int argb = (255<<24) + ((int)(red*correction)<<16) + ((int)(green*correction)<<8) + (int)(blue*correction);
					res.getPixelWriter().setArgb(x, y, argb);
				} else {
					gray = (int) Math.min(255, Math.max(0, gray*correction));
					res.getPixelWriter().setColor(x, y, Color.grayRgb(gray));
				}
			}
		return res;
	}
	
	public Image greyScale(Image img) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		WritableImage res = new WritableImage(width, height);
		PixelWriter writer = res.getPixelWriter();
		
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++)
				writer.setColor(x, y, img.getPixelReader().getColor(x, y).grayscale());
		
		return res;
	}
}