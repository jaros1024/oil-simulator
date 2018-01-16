package pl.alphabyte.oilSimulator;

import java.awt.*;

/**
 * Represents single point (one cell with 1 px size)
 */

public class Point {

	public static final Point DEAD_POINT = new Point();
	private static final Color[] colors = {new Color(0xB6C7FA), new Color(0x027306), new Color(0xff4907)};

	private Point [] neighbors;
	private double [] calculationParams;

	private static final double EVAPORIZATION = 0.002; // evaporization ratio
	private static final double MIN_OIL = 0.000001;
	public static final double MAX_OIL = 10;

	/* directions array [4]
	    0 - down
        1 - right
        2 - left
        3 - up
    */

	private int neighborsCount;

	// 0 - NO OIL
	// 10 - MOST OIL
	private double oilLevel = 0;
	private double nextOilLevel;

	// 0 - water
    // 1 - land
	private int type = 0;
	
	public Point() {
		oilLevel = 0;
		nextOilLevel = 0;
		neighborsCount = 0;
		neighbors = new Point[4];
		calculationParams = new double[4];
		for (int i = 0; i < calculationParams.length; i++) {
			calculationParams[i] = 2;
        }
	}

	public void setOilLevel(double oilLevel) {
		this.oilLevel = oilLevel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void clicked(double intensity) {
		oilLevel = intensity;
	}
	
	public double getState() {
		return oilLevel;
	}

	/**
	 * Changes current parameters array
	 * @param paramChanges Changes of each parameter
	 */
	public void modifyCalculationParams(double[] paramChanges){
	    for (int i = 0; i < calculationParams.length; i++){
	        calculationParams[i] += paramChanges[i];
        }
    }

	/**
	 * Calculates new state, basing on type
	 */
	public void calculateNewState() {
		if (type == 1) {
            calculateNewStateOnLand();
		} else {
			calculateNewStateOnWater();
		}
		nextOilLevel = nextOilLevel*(1-EVAPORIZATION);
		if (nextOilLevel < MIN_OIL) nextOilLevel = 0;
    }

	/**
	 * Calculates new state if the point type is water
	 */
	public void calculateNewStateOnWater(){
	    double sum = 0;
	    double paramSum = 0;
	    for (int i = 0; i < neighbors.length; i++){
	        sum += calculationParams[i]*neighbors[i].getState();
	        paramSum += calculationParams[i];
        }
        nextOilLevel = sum / paramSum;


	}

	/**
	 * Calculates new state if the point type is land
	 */
	public void calculateNewStateOnLand(){
	    double sum = 0;
	    int b = 50;

        for (Point item : neighbors) {
            sum += item.getState();
        }
        nextOilLevel = (b * oilLevel + sum) / (b+4);
    }

	/**
	 * Changes state to newly calculated
	 */
	public void changeState() {
		oilLevel = nextOilLevel;
	}

	/**
	 * Adds new neightbor
	 * @param nei A neighbor to add
	 */
	public void addNeighbor(Point nei) {
		neighbors[neighborsCount] = nei;
		neighborsCount ++;
	}

	/**
	 * Gets amount of neighbors
	 * @return Amount of neighbors
	 */
	public int countNeighbors(){
		return neighborsCount;
	}

	/**
	 * Calculates and returns a color of the point
	 * @return Color
	 */
	public Color getColor(){
		if(oilLevel == 0) {
			return colors[type];
		}
		if(type == 1 && oilLevel != MAX_OIL){
			return getMixedColor(colors[2], colors[1], oilLevel);
		}

		float[] hsb = Color.RGBtoHSB(
				colors[2].getRed(),
				colors[2].getGreen(),
				colors[2].getBlue(),
				null);

		float b = hsb[2];
		b -= (oilLevel / MAX_OIL) * b;
		return Color.getHSBColor(hsb[0], hsb[1], b);
	}

	/**
	 * Mixes two colors with given proportions
	 * @param c1 First color
	 * @param c2 Second color
	 * @param p Proportions, where used is p of first color and (1-p) of second color
	 * @return Mixed color
	 */
	private Color getMixedColor(Color c1, Color c2, double p){
		p = p/MAX_OIL;

		int newRed = mixColor(c1.getRed(), c2.getRed(), p);
		int newGreen = mixColor(c1.getGreen(), c2.getGreen(), p);
		int newBlue = mixColor(c1.getBlue(), c2.getBlue(), p);

		return new Color(newRed, newGreen, newBlue);
	}

	/**
	 * Mixes two double values with given proportions
	 * @param v1 First value
	 * @param v2 Second value
	 * @param p Proportions, where used is p of first value and (1-p) of second value
	 * @return Mixed value
	 */
	private int mixColor(double v1, double v2, double p){
		Double newValue = (v1 * p) + (v2 * (1-p));
		return (int)Math.floor(newValue);
	}
}
