package pl.alphabyte.oilSimulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, Serializable {
	private transient Point[][] points;
	private Vector<OceanCurrent> currentVector = new Vector<OceanCurrent>();
	private Vector<Wind> windVector = new Vector<Wind>();

	private transient BoardCache cache;
	private transient BufferedImage inputImage;

	private transient OceanCurrent.Factory currentFactory;
	private transient Wind.Factory windFactory;

	public static final int IMAGE_WATER_COLOR = -1;
	private static final int CLICK_RADIUS = 2;

	/* LISTA TRYBÓW
	   0 - DOMYŚLNY, KLIKNIĘCIE MALUJE ROPE
	   1 - KLIKNIĘCIE POWODUJE ZEZWOLENIE NA RYSOWANIE PRĄDU MORSKIEGO
	   2 - KLIKNIĘCIE POWODUJE ZEZWOLENIE NA RYSOWANIE WIATRU
	*/
	private static int addingMode = 0;

	public Board(BufferedImage img) {
		inputImage = img;
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);

		initialize();
	}

	private void initialize() {
		Point tmp = new Point();
		points = IOHelper.loadPointsFromImage(inputImage);
		cache = new BoardCache(this, points[0].length, points.length);
		currentFactory = new OceanCurrent.Factory(this);
		windFactory = new Wind.Factory(this);

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				for(int i = (x-1); i<=(x+1); i++){
					for(int j = (y-1); j<=(y+1); j++){
						try {
							if((i == x && j==y) || (Math.abs(i-x) + Math.abs(j-y) == 2)) continue;
							tmp = points[i][j];
							points[x][y].addNeighbor(tmp);
						}
						catch(ArrayIndexOutOfBoundsException e){
							tmp = Point.DEAD_POINT;
							points[x][y].addNeighbor(tmp);
						}
					}
				}

				int neighborCount = points[x][y].countNeighbors();
				if(neighborCount != 4) {
					System.out.println("Error! Incorrect number of neighbors!" + neighborCount);
				}
			}
		}
	}

	// single iteration
	public void iteration() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].calculateNewState();

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].changeState();
		this.repaint();
	}

	// clearing board
	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].setOilLevel(0);
			}
		this.repaint();
	}

	private void drawPoints(Graphics g) {
		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				g.setColor(points[x][y].getColor());
				g.fillRect(y, x, 1, 1);
			}
		}

	}

	private void drawCurrents(Graphics g){
		for(OceanCurrent oc : currentVector){
			oc.draw(g);
		}
	}

	private void drawWinds(Graphics g){
		for(Wind w : windVector){
			w.draw(g);
		}
	}

	private void drawAll(Graphics g){
		drawPoints(g);
		drawCurrents(g);
		drawWinds(g);
	}

	private Integer[] getNeighborhood(int x, int y){
		Integer[] result = new Integer[4];

		//x1
		result[0] = x-CLICK_RADIUS;
		if(result[0] < 0) result[0] = 0;

		//y1
		result[1] = y-CLICK_RADIUS;
		if(result[1] < 0) result[1] = 0;

		//x2
		result[2] = x+CLICK_RADIUS;
		if(result[2] >= points[0].length) result[2] = points[0].length-1;

		//y2
		result[3] = y+CLICK_RADIUS;
		if(result[3] >= points.length) result[3] = points.length-1;

		return result;
	}

	private void toggleNeighborhood(Integer[] position){
		for(int i=position[0]; i<=position[2]; i++){
			for(int j=position[1]; j<=position[3]; j++){
				points[j][i].clicked();
			}
		}
		int window = CLICK_RADIUS*2+1;
		this.repaint(0, position[0], position[1], window, window);
	}

	protected void paintComponent(Graphics g) {
		if(addingMode == 0) {
			drawAll(g);
		}
		if(addingMode > 0) {
			cache.getFromCache(g);
			if (currentFactory.isBeingCreated()) {
				currentFactory.getTmpCurrent().draw(g);
			}
			if (windFactory.isBeingCreated()) {
				windFactory.getTmpWind().draw(g);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(addingMode == 0) {
			toggleNeighborhood(getNeighborhood(e.getX(), e.getY()));
		}
		else {
			if(currentFactory.isBeingCreated() && e.getButton() == 3) {
				currentVector.add(currentFactory.saveCurrent());
			}
			else {
				currentFactory.handleMouseClick(e);
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		// only adding wind and idle state use mouseDragged event, so we need to consider only these 2 cases
		if(addingMode == 0) {
			toggleNeighborhood(getNeighborhood(e.getX(), e.getY()));
		}
		else if(addingMode == 1 && currentFactory.isBeingCreated()){
			currentFactory.handleMouseMove(e);
		}
		else if(addingMode == 2){
			windFactory.handleMouseDragged(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(windFactory.isBeingCreated()) {
			windVector.add(windFactory.saveWind(e.getPoint()));
		}
		else if(currentFactory.isBeingCreated() && e.getButton() == 1){
			currentFactory.addNextPoint(e.getPoint());
		}
	}

	public void mouseMoved(MouseEvent e) {
		// only OceanCurrent Factory uses mouseMoved event
		if(currentFactory.isBeingCreated()){
			currentFactory.handleMouseMove(e);
		}
	}

	public void setAddingMode(int addingMode) {
		Board.addingMode = addingMode;
		if(addingMode > 0){
			cache.updateCache();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageIO.write(inputImage, "png", out);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inputImage = ImageIO.read(in);
		initialize();
	}

	void getCacheImage(Graphics g){
		drawAll(g);
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}