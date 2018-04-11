package imageManipulation;

import basic.Point;
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
	
	public Image scale(Image img, double factor, ScalingType scalingType) {
		int width = (int) (img.getWidth()*factor);
		int height = (int) (img.getHeight()*factor);
		
		System.out.println("scaling image by factor " + factor);
		System.out.println("old: " + img.getWidth() + ", " + img.getHeight());
		System.out.println("new: " + width + ", " + height);
		
		WritableImage res = new WritableImage(width, height);
		PixelWriter writer = res.getPixelWriter();
		PixelReader reader = img.getPixelReader();
		
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {
				Color color;
				switch (scalingType) {
				case NEAREST_NEIGHBOR:
					color = reader.getColor((int) (x*img.getWidth()/width), (int) (y*img.getHeight()/height));
					break;
				case BILINEAR:
					Point np = new Point(x*(img.getWidth()/width), y*(img.getHeight()/height));
					Point p1 = new Point(Math.floor(np.x), Math.floor(np.y));
					Point p2 = new Point(Math.min(img.getWidth()-1, p1.x+1), Math.min(img.getHeight()-1, p1.y+1));
					
					if(p1.x==p2.x || p1.y==p2.y) {
						color = reader.getColor((int) p1.x, (int) p2.y);
						continue;
					}
//					System.out.println(np);
//					System.out.println(p1);
//					System.out.println(p2);
					
					//for detail see wikipedia/Bilinear Interpolation
					double a = 1/((p2.x-p1.x)*(p2.y-p1.y));
					double a11 = (p2.x-np.x)*(p2.y-np.y);
					double a12 = (p2.x-np.x)*(np.y-p1.y);
					double a21 = (np.x-p1.x)*(p2.y-np.y);
					double a22 = (np.x-p1.x)*(np.y-p1.y);
					
//					System.out.println(a + ", " + a11 + ", " + a12 + ", " + a21 + ", " + a22);
					
					double red = a*(a11*reader.getColor((int) p1.x, (int) p1.y).getRed() +
							a12*reader.getColor((int) p1.x, (int) p2.y).getRed() +
							a21*reader.getColor((int) p2.x, (int) p1.y).getRed() +
							a22*reader.getColor((int) p2.x, (int) p2.y).getRed());
					double green = a*(a11*reader.getColor((int) p1.x, (int) p1.y).getGreen() +
							a12*reader.getColor((int) p1.x, (int) p2.y).getGreen() +
							a21*reader.getColor((int) p2.x, (int) p1.y).getGreen() +
							a22*reader.getColor((int) p2.x, (int) p2.y).getGreen());
					double blue = a*(a11*reader.getColor((int) p1.x, (int) p1.y).getBlue() +
							a12*reader.getColor((int) p1.x, (int) p2.y).getBlue() +
							a21*reader.getColor((int) p2.x, (int) p1.y).getBlue() +
							a22*reader.getColor((int) p2.x, (int) p2.y).getBlue());
					double aplha = a*(a11*reader.getColor((int) p1.x, (int) p1.y).getOpacity() +
							a12*reader.getColor((int) p1.x, (int) p2.y).getOpacity() +
							a21*reader.getColor((int) p2.x, (int) p1.y).getOpacity() +
							a22*reader.getColor((int) p2.x, (int) p2.y).getOpacity());
					
//					System.out.println(red + ", " + green + ", " + blue + ", " + aplha);
					
					if(p1.x==p2.x || p1.y==p2.y) {
						System.out.println("x: " + x + ", y: " + y);
						System.out.println(np);
						System.out.println(p1);
						System.out.println(p2);
						System.out.println(a + ", " + a11 + ", " + a12 + ", " + a21 + ", " + a22);
						System.out.println(red + ", " + green + ", " + blue + ", " + aplha);
					}
					
					color = Color.rgb((int)(red*255), (int)(green*255), (int)(blue*255), Math.min(1, Math.max(0, aplha)));					
					break;
				default:
					color = Color.BLACK;
					break;
				}
				writer.setColor(x, y, color);
			}
				
		return res;
	}
}

enum ScalingType {
		NEAREST_NEIGHBOR, BILINEAR;
}