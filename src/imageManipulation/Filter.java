package imageManipulation;

public class Filter {

	//Blurr
	public static Filter gausBlur = new Filter(new double[][] {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 1.0/16.0, true);
	public static Filter meanBlur = new Filter(new double[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 1.0/9.0, true);
	
	//Edge Detection
	public static Filter sobelH = new Filter(new double[][] {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}});
	public static Filter sobelV = new Filter(new double[][] {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}});
	public static Filter scharrV = new Filter(new double[][] {{3, 10, 3}, {0, 0, 0}, {-3, -10, -3}});
	public static Filter scharrH = new Filter(new double[][] {{3, 0, -3}, {10, 0, -10}, {3, 0, -3}});
	public static Filter laplaceA = new Filter(new double[][] {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}}, 1.0/9.0);
	
	public double[][] kernel;
	public double correction;
	public boolean color;
	
	public Filter(double[][] kernel, double correction, boolean color) {
		this.kernel = kernel;
		this.correction = correction;
		this.color = color;
	}
	
	public Filter(double[][] kernel, boolean color) {
		this(kernel, 1, color);
	}
	
	public Filter(double[][] kernel, double correction) {
		this(kernel, correction, false);
	}
	
	public Filter(double[][] kernel) {
		this(kernel, 1, false);
	}
		
}
