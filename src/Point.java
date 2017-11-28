import java.awt.*;
import java.util.ArrayList;

public class Point {

	public static final Point DEAD_POINT = new Point();
	private static final Color[] colors = {new Color(0xB6C7FA), new Color(0x027306)};

	private ArrayList<Point> neighbors;


	// 0 - NO OIL
	// 10 - MOST OIL
	private double oilLevel = 0;
	private double nextOilLevel;
	private double initialState = 10;

	// 0 - water
    // 1 - land
	private int type = 0;
	
	public Point() {
		oilLevel = 0;
		nextOilLevel = 0;
		neighbors = new ArrayList<Point>();
	}

	public void setOilLevel(double oilLevel) {
		this.oilLevel = oilLevel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void clicked() {
		oilLevel = initialState;
	}
	
	public double getState() {
		return oilLevel;
	}

	public void calculateNewState() {
		float sum = 0;
		for (Point item: neighbors) {
			sum += item.getState();
		}
		nextOilLevel = (sum + oilLevel)/9;
		//if (nextState < 0.5) nextState = 0;
	}

	// prawdopodobnie tutaj trzeba zmienić state na oilLevel
	public void changeState() {
		oilLevel = nextOilLevel;
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}


	public int countNeighbors(){
		return neighbors.size();
	}

	public Color getColor(){
		float[] hsb = Color.RGBtoHSB(
				colors[type].getRed(),
				colors[type].getGreen(),
				colors[type].getBlue(),
				null);

		float b = hsb[2];
		b = b -((float)oilLevel/10)*b;
		return Color.getHSBColor(hsb[0], hsb[1], b);
	}
}
