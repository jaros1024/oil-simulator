import java.awt.*;
import java.util.ArrayList;

public class Point {

	public static final Point DEAD_POINT = new Point();
	private static final Color[] colors = {new Color(0xB6C7FA), new Color(0x027306), new Color(0xff4907)};

	private Point [] neighbors;
	private int neighborsCount;

	// 0 - NO OIL
	// 10 - MOST OIL
	private double oilLevel = 0;
	private double nextOilLevel;

	private double maxOil = 20;

	// 0 - water
    // 1 - land

	private int type = 0;
	
	public Point() {
		oilLevel = 0;
		nextOilLevel = 0;
		neighborsCount = 0;
		neighbors = new Point[4];
	}

	public void setOilLevel(double oilLevel) {
		this.oilLevel = oilLevel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void clicked() {
		oilLevel = maxOil;
	}
	
	public double getState() {
		return oilLevel;
	}

	public void calculateNewState() {
		calculate2();
	}

	public void calculate1(){
		float sum = 0;
		for (Point item: neighbors) {
			sum += 2*item.getState();
		}
		if(type == 1){
			nextOilLevel =  (4*oilLevel + sum/2)/8;
		} else {
			nextOilLevel = (sum)/8;
		}

	}

	public void calculate2() {
		double sum = 0;
		int a = 2;
		int b = 100;
		if (type == 1) {
			for (Point item : neighbors) {
				sum += item.getState();
			}
			nextOilLevel = (b * oilLevel + sum) / (b+4);
		} else {
			sum = a * neighbors[0].getState() +
					a * neighbors[1].getState() + a * neighbors[2].getState() +
					a * neighbors[3].getState();
			sum += oilLevel;
					nextOilLevel = sum/(a*4 + 1);
		}
		if (nextOilLevel < 0.01) nextOilLevel = 0;
	}

	// prawdopodobnie tutaj trzeba zmienić state na oilLevel
	public void changeState() {
		oilLevel = nextOilLevel;
	}
	
	public void addNeighbor(Point nei) {
		neighbors[neighborsCount] = nei;
		neighborsCount ++;
	}


	public int countNeighbors(){
		return neighborsCount;
	}

	public Color getColor(){
		if(oilLevel == 0) {
			return colors[type];
		}

		float[] hsb = Color.RGBtoHSB(
				colors[2].getRed(),
				colors[2].getGreen(),
				colors[2].getBlue(),
				null);

		float b = hsb[2];
		b -= (oilLevel / maxOil) * b;
		return Color.getHSBColor(hsb[0], hsb[1], b);
	}
}
