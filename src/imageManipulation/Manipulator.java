package imageManipulation;

import java.nio.IntBuffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public class Manipulator {

	private Image img;
	private int width, height;
	
	public Manipulator(String imgPath) {
		img = new Image(imgPath);
		width = (int) img.getWidth();
		height = (int) img.getWidth();
	}
	
	public Manipulator(Image img) {
		this.img = img;
		width = (int) img.getWidth();
		height = (int) img.getHeight();		
	}
	
	public Image applyFilter(double[][] filter) {
		WritableImage res = new WritableImage(width, height);
		
		double correction = 0;
		for(int w=0; w<filter.length; w++)
			for(int h=0; h<filter[w].length; h++)
				correction += filter[w][h];
		
		if (correction != 0)
				correction = 1/correction;
				
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {
				int red=0, green=0, blue=0;
				
				for(int w=0; w<filter.length; w++)
					for(int h=0; h<filter[w].length; h++) {
						int posX = x -filter.length/2 +w;
						int posY = y -filter[w].length/2 + h;
						
//						System.out.println(x + " " + y + " " + w + " " + h);
						
						if(posX < 0 || posX >= width || posY < 0 || posY >= height)
							continue;
						
						int argb = img.getPixelReader().getArgb(posX, posY);
						red += filter[w][h] * ((0xff<<16 & argb)>>>16);
						green += filter[w][h] * ((0xff<<8 & argb)>>>8);
						blue += filter[w][h] * (0xff & argb);
						
					}
				int argb = (int) ((255<<24) + (red<<16)*correction + (green<<8)*correction + (blue)*correction);
				res.getPixelWriter().setArgb(x, y, argb);
			}
		
		System.out.println("correction: " + correction);
		
		return res;
	}

}