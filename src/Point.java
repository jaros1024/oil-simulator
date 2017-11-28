import java.awt.*;
import java.util.ArrayList;

public class Point {

	public static final Point DEAD_POINT = new Point();
	private static final Color[] colors = {new Color(0xB6C7FA), new Color(0x027306)};

	private ArrayList<Point> neighbors;
	private int currentState;
	private int nextState;
	private int numStates = 6;

	// 0 - NO OIL
	// 10 - MOST OIL
	private double oilLevel = 0;

	// 0 - water
    // 1 - land
	private int type = 0;
	
	public Point() {
		currentState = 0;
		nextState = 0;
		neighbors = new ArrayList<Point>();
	}

	public void setOilLevel(double oilLevel) {
		this.oilLevel = oilLevel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void clicked() {
		oilLevel = 10;
	}
	
	public int getState() {
		return currentState;
	}

	public void calculateNewState() {
		// TODO obliczenie nowego poziomu ropy
		int activeNeighbors = countActiveNeighbors();

		//for alive cell
		if(getState() > 0){
			if(activeNeighbors == 2 || activeNeighbors == 3)
				nextState = 1;
			else
				nextState = 0;
		}
		//for dead cell
		else {
			if(activeNeighbors == 3)
				nextState = 1;
			else
				nextState = 0;
		}

	}

	// prawdopodobnie tutaj trzeba zmienić state na oilLevel
	public void changeState() {
		currentState = nextState;
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
	
	public int countActiveNeighbors(){
		int counter = 0;
		for(Point p : neighbors){
			if(p.getState() > 0)
				counter++;
		}
		return counter;
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
		b = b-((float)oilLevel/10)*b;
		return Color.getHSBColor(hsb[0], hsb[1], b);
	}
}
