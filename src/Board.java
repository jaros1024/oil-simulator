import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private Point[][] points;

	private static final String IMG_FILE = "map.bmp";
	private static final int IMAGE_WATER_COLOR = -1;
	private static final int CLICK_RADIUS = 2;

	public Board() {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);

		initialize();
	}

	private Point[][] loadPointsFromImage(){
		BufferedImage img;
		Point[][] result = null;
		try {
			img = ImageIO.read(new File(IMG_FILE));
			//
			int imgHeight = img.getHeight();
			int imgWidth = img.getWidth();
			result = new Point[imgHeight][imgWidth];

			for(int i=0; i<imgHeight; i++){
				for(int j=0; j<imgWidth; j++){
					int color = img.getRGB(j, i);

					Point p = new Point();
					if(color != IMAGE_WATER_COLOR) {
						p.setType(1);
					}
					else {
						p.setType(0);
					}
					result[i][j] = p;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
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

	private void initialize() {
		points = loadPointsFromImage();

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				for(int i = (x-1); i<=(x+1); i++){
					for(int j = (y-1); j<=(y+1); j++){
						if(i == x && j == y)
							continue;

						try {
							points[x][y].addNeighbor(points[i][j]);
						}
						catch(ArrayIndexOutOfBoundsException e){
							points[x][y].addNeighbor(Point.DEAD_POINT);
						}
					}
				}

				int neighborCount = points[x][y].countNeighbors();
				if(neighborCount != 8)
					System.out.println("Error! Incorrect number of neighbors!");

			}
		}
	}

	private void drawPoints(Graphics g) {
		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				g.setColor(points[x][y].getColor());
				g.fillRect(y, x, 1, 1);
			}
		}

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
		drawPoints(g);
	}

	public void mouseClicked(MouseEvent e) {
		toggleNeighborhood(getNeighborhood(e.getX(), e.getY()));
	}

	public void componentResized(ComponentEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		toggleNeighborhood(getNeighborhood(e.getX(), e.getY()));
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
