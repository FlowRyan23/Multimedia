package imageManipulation;

public class Filter {

	//Blurr
	public static Filter gausBlur = new Filter("gausBlur", new double[][] {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 1.0/16.0, true);
	public static Filter meanBlur = new Filter("meanBlur", new double[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 1.0/9.0, true);
	
	//Edge Detection
	public static Filter sobelH = new Filter("sobelH", new double[][] {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}});
	public static Filter sobelV = new Filter("sobelV", new double[][] {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}});
	public static Filter scharrV = new Filter("scharrV", new double[][] {{3, 10, 3}, {0, 0, 0}, {-3, -10, -3}});
	public static Filter scharrH = new Filter("scharrH", new double[][] {{3, 0, -3}, {10, 0, -10}, {3, 0, -3}});
	public static Filter laplaceA = new Filter("laplaceA", new double[][] {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}}, 1.0/9.0);
	
	public String name;
	public double[][] kernel;
	public double correction;
	public boolean color;
	
	public Filter(String name, double[][] kernel, double correction, boolean color) {
		this.name = name;
		this.kernel = kernel;
		this.correction = correction;
		this.color = color;
	}
	
	public Filter(String name, double[][] kernel, boolean color) {
		this(name, kernel, 1, color);
	}
	
	public Filter(String name, double[][] kernel, double correction) {
		this(name, kernel, correction, false);
	}
	
	public Filter(String name, double[][] kernel) {
		this(name, kernel, 1, false);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Filter[] allFilters() {
		return new Filter[]{gausBlur, meanBlur, sobelH, sobelV, scharrV, scharrH, laplaceA};
	}
		
}
