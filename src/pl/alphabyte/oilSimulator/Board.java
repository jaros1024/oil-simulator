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
	private transient Statistics stats;
	private double scale;
	private transient double intensity = Point.MAX_OIL;
	private transient boolean[][] contamined;

	private transient BoardCache cache;
	private transient BufferedImage inputImage;

	private transient OceanCurrent.Factory currentFactory;
	private transient Wind.Factory windFactory;

	private static final int CLICK_RADIUS = 2;
	private static final double REAL_PIXEL_SIZE = 2.275;
	private static final int ITERATIONS_PER_DAY = 9;

	/* MODE LIST
	   0 - DEFAULT, SINGLE CLICK IS PAINTING OIL
	   1 - SINGLE CLICK IS STARTING AN OCEAN CURRENT FACTORY
	   2 - DRAGGING IS CREATING A WIND
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

	/**
	 * Initializes board with points, creates winds and currents, creates the statistics of contamination
	 */
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

		for(OceanCurrent current : currentVector){
			currentFactory.applyCurrent(current);
		}

		for(Wind wind : windVector){
			windFactory.applyWind(wind);
		}
		contamined = new boolean[points.length][points[0].length];
	}

	public Point getPoint(int x, int y){
	    return points[y][x];
    }

    public void setPoint(int x, int y, Point point){
        points[y][x] = point;
    }

    public int getXSize(){
    	return points[12].length;
	}

	public int getYSize(){
		return points.length;
	}

	/**
	 * Single iteration of cellular automata
	 * @param iterNum Iteration number
	 */
	public void iteration(int iterNum) {
		int nonZero = 0;
		double volume = 0;

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].calculateNewState();

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].changeState();
				volume += points[x][y].getState()*getRealPixelExpanse()*1000;
				if(points[x][y].getState() != 0){
					++nonZero;
					contamined[x][y] = true;
				}
			}

		this.repaint();
		stats.setDays((int) Math.floor(iterNum/ITERATIONS_PER_DAY));
		stats.setCurrentExpanse(nonZero*getRealPixelExpanse());
		stats.setAllExpanse(countContamined()*getRealPixelExpanse());
		stats.setVolume(volume);
	}

	/**
	 * Clears the board from the oil
	 */
	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].setOilLevel(0);
			}
		stats.setAllExpanse(0);
		clearContaminated();
		this.repaint();
	}

	/**
	 * Draws all the points on the board
	 * @param g @see java.awt.Graphics
	 */
	private void drawPoints(Graphics g) {
		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				g.setColor(points[x][y].getColor());
				g.fillRect(y, x, 1, 1);
			}
		}

	}

	/**
	 * Draws all the currents on the board.
	 * @param g @see java.awt.Graphics
	 */
	private void drawCurrents(Graphics g){
		for(OceanCurrent oc : currentVector){
			oc.draw(g);
		}
	}

	/**
	 * Draws all the winds on the board.
	 * @param g @see java.awt.Graphics
	 */
	private void drawWinds(Graphics g){
		for(Wind w : windVector){
			w.draw(g);
		}
	}

	/**
	 * Draws all elements of the board.
	 * @param g @see java.awt.Graphics
	 */
	private void drawAll(Graphics g){
		drawPoints(g);
		drawCurrents(g);
		drawWinds(g);
	}

	/**
	 * Returns neighborhood of point with given coordinates
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @return Array containing the neighborhood
	 */
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

	/**
	 * Marks given neighborhood as clicked
	 * @param position Array of positions to mark
	 */
	private void toggleNeighborhood(Integer[] position){
		for(int i=position[0]; i<=position[2]; i++){
			for(int j=position[1]; j<=position[3]; j++){
				points[j][i].clicked(intensity);
			}
		}
		int window = CLICK_RADIUS*2+1;
		this.repaint(0, position[0], position[1], window, window);
	}

	/**
	 * Paints the board and elements that are currently being created
	 * @param g @see java.awt.Graphics
	 */
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

	/**
	 * Handles mouse click on the board
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if(addingMode == 0 && e.getX() <= points[0].length && e.getY() <= points.length) {
			toggleNeighborhood(getNeighborhood(e.getX(), e.getY()));
		}
		else if(addingMode == 1){
			if(currentFactory.isBeingCreated() && e.getButton() == 3) {
				currentVector.add(currentFactory.saveCurrent());
			}
			else {
				currentFactory.handleMouseClick(e);
			}
		}
	}

	/**
	 * Handles dragging the mouse through the board
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
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

	/**
	 * Handles the mouse releasing event
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if(windFactory.isBeingCreated()) {
			windVector.add(windFactory.saveWind(e.getPoint()));
		}
		else if(currentFactory.isBeingCreated() && e.getButton() == 1){
			currentFactory.addNextPoint(e.getPoint());
		}
	}

	/**
	 * Handles the mouse moving event
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
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

	/**
	 * Marks all the points as uncontaminated
	 */
	private  void clearContaminated(){
		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				contamined[x][y] = false;
			}
		}
	}

	/**
	 * Calculates real pixel size
	 * @return Real pixel size
	 */
	private double getRealPixelSize(){
		return REAL_PIXEL_SIZE / scale;
	}

	/**
	 * Calculates real pixel expanse
	 * @return Real pixel expanse
	 */
	public double getRealPixelExpanse(){
		return Math.pow(getRealPixelSize(), 2);
	}

	/**
	 * Counts contaminated cells
	 * @return Number of contaminated cells
	 */
	private int countContamined(){
		int result = 0;
		for(int i = 0; i<contamined.length;i++){
			for(int j = 0; j<contamined[i].length; j++){
				if(contamined[i][j]){
					++result;
				}
			}
		}
		return result;
	}

	/**
	 * Prepares graphics to cache
	 * @param g @see java.awt.Graphics
	 */
	void getCacheImage(Graphics g){
		drawAll(g);
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setStats(Statistics stats){
		this.stats = stats;
		stats.setScale(getRealPixelExpanse());
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}
}

