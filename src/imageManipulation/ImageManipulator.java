package imageManipulation;

import java.util.Stack;

import basic.BaseKit;
import basic.IntPoint;
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
		double[][][] kernel = filter.kernel;
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
						red += kernel[Filter.red][w][h] * ((0xff<<16 & argb)>>>16);
						green += kernel[Filter.green][w][h] * ((0xff<<8 & argb)>>>8);
						blue += kernel[Filter.blue][w][h] * (0xff & argb);
						
						gray += kernel[0][w][h] * pixelReader.getColor(posX, posY).grayscale().getBrightness() * 255;					
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
	
	public Image pixelwiseOp(Image img, PixelOp op) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		WritableImage res = new WritableImage(width, height);
		PixelReader reader = img.getPixelReader();
		PixelWriter writer = res.getPixelWriter();
		
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++)
				switch (op) {
				case GREY_SCALE:
					writer.setColor(x, y, reader.getColor(x, y).grayscale());
					break;
				case INVERT:
					writer.setColor(x, y, reader.getColor(x, y).invert());
					break;				
				default:
					break;
				}
		
		return res;
	}
	
	public Image colorNormalization(Image img) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		Image copie = pixelwiseOp(img, PixelOp.GREY_SCALE);
		PixelReader copieReader = copie.getPixelReader();
		WritableImage res = new WritableImage(width, height);
		PixelReader reader = img.getPixelReader();
		PixelWriter writer = res.getPixelWriter();
		
		double max=0, min=1;
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {
				double pixelVal = copieReader.getColor(x, y).getBlue();
				if(pixelVal > max) max = pixelVal;
				if(pixelVal < min) min = pixelVal;
			}
		
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++) {				
				double red = reader.getColor(x, y).getRed();
				double green = reader.getColor(x, y).getGreen();
				double blue = reader.getColor(x, y).getBlue();
				
				int nRed = (int) (((red-min)*255/(max-min)));
				int nGreen = (int) (((green-min)*255/(max-min)));
				int nBlue = (int) (((blue-min)*255/(max-min)));
							
				writer.setColor(x, y, Color.rgb(BaseKit.bound(0, nRed, 255), BaseKit.bound(0, nGreen, 255), BaseKit.bound(0, nBlue, 255)));
			}
		
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
	
	public Image floodFill(Image img, int x, int y, Color fillColor, double toleranz, boolean square) {
		IntPoint imgSize = new IntPoint((int) img.getWidth()-1, (int) img.getHeight()-1);
		WritableImage res = new WritableImage(img.getPixelReader(), imgSize.x+1, imgSize.y+1);
		PixelReader reader = res.getPixelReader();
		PixelWriter writer = res.getPixelWriter();		
		Color sourceColor = reader.getColor(x, y);
		
		System.out.println("filling " + sourceColor + " from (" + x + ", " + y + ") with " + fillColor + " in " + (square?"square":"diamond") + "-mode");
		
		Stack<IntPoint> openList = new Stack<>();
		openList.push(new IntPoint(x, y));
		
		int count = 0;
		IntPoint pos;
		while(!openList.isEmpty()) {
			pos = openList.pop();
			
			if(BaseKit.inBounds(toleranz, 0., 1.) && toleranz > 0) {
				if(!pos.inBounds(imgSize) || !BaseKit.isWithinToleranz(reader.getColor(pos.x, pos.y), sourceColor, toleranz) || reader.getColor(pos.x, pos.y).equals(fillColor))
					continue;
			} else {
				if(!pos.inBounds(imgSize) || !reader.getColor(pos.x, pos.y).equals(sourceColor) || reader.getColor(pos.x, pos.y).equals(fillColor))
					continue;
			}
			
			openList.push(new IntPoint(pos.x -1, pos.y));
			openList.push(new IntPoint(pos.x, pos.y -1));
			openList.push(new IntPoint(pos.x +1, pos.y));
			openList.push(new IntPoint(pos.x, pos.y +1));
			
			if(square) {
				openList.push(new IntPoint(pos.x -1, pos.y -1));
				openList.push(new IntPoint(pos.x -1, pos.y +1));
				openList.push(new IntPoint(pos.x +1, pos.y -1));
				openList.push(new IntPoint(pos.x +1, pos.y +1));
			}
			count++;
			writer.setColor(pos.x, pos.y, fillColor);
		}
		
		System.out.println("filled " + count + " pixels");
		return res;
	}
	
}

enum ScalingType {
	NEAREST_NEIGHBOR, BILINEAR;
}

enum PixelOp {
	GREY_SCALE, INVERT, COLOR_NORMALISATION_GS;
}

enum ToolCode {
	FLOOD_FILL;
}