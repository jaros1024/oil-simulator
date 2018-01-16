package pl.alphabyte.oilSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The main class that creates all the frames and defines base values
 */

public class Program extends JFrame {

	/**
	 * Frame name
	 */
	public static final String WINDOW_NAME = "Oil Simulator";
	private GUI gui;
	private BufferedImage image;
	Object lock = new Object();

	/**
	 * The constructor carries on all the board creating process and uses a lock to block between certain stages
	 */
	public Program() {
		setTitle(WINDOW_NAME);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		String serializedBoard = null;

		InitialWindow iw = new InitialWindow(this);
		iw.initialize();
		setContentPane(iw.panel1);
		this.setSize(640, 480);
		this.setVisible(true);

		synchronized (lock){
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.setVisible(false);
		setContentPane(new Container());

		serializedBoard = iw.getFileToLoad();

		MapSelector ms = null;
		if(serializedBoard == null) {
			ms = new MapSelector(this);
			ms.initialize();
			setContentPane(ms.panel1);
			this.setSize(1000, 700);
			this.setVisible(true);

			synchronized (lock) {
				while (image == null) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			this.setVisible(false);
			setContentPane(new Container());
		}

		gui = new GUI(this);
		gui.initialize(this.getContentPane(), serializedBoard);

		this.setSize(1024, 768);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);

		if(ms != null) {
			gui.setScale(ms.getScale());
		}
		this.setVisible(true);
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image){
		this.image = image;
		lock.notify();
	}

	public static void main(String[] args) {
		new Program();
	}

}
