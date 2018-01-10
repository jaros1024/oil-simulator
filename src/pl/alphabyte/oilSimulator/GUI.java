package pl.alphabyte.oilSimulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class containing GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
	private Timer timer;
	private Board board;
	private JButton start;
	private JButton clear;
	private JButton addWind;
	private JButton addCurrent;
	private JButton saveBtn;
	private JSlider pred;
	private JSlider intensity;
	private JFrame frame;
	private Statistics stats;
	private int iterNum = 0;
	private final int maxDelay = 500;
	private final int initDelay = 100;
	private boolean running = false;

	public GUI(JFrame jf) {
		frame = jf;
		timer = new Timer(initDelay, this);
		timer.stop();
	}

	/**
	 * @param container to which GUI and board is added
	 */
	public void initialize(Container container, String serializedBoard) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(container.getWidth(), container.getHeight()));

		JPanel buttonPanel = new JPanel();

		start = new JButton("Start");
		start.setActionCommand("Start");
		start.setToolTipText("Starts clock");
		start.addActionListener(this);

		clear = new JButton("Clear");
		clear.setActionCommand("clear");
		clear.setToolTipText("Clears the board");
		clear.addActionListener(this);

		pred = new JSlider();
		pred.setMinimum(0);
		pred.setMaximum(maxDelay);
		pred.setToolTipText("Time speed");
		pred.addChangeListener(this);
		pred.setValue(maxDelay - timer.getDelay());

		intensity = new JSlider();
		intensity.setMinimum(1);
		intensity.setMaximum((int)Point.MAX_OIL);
		intensity.setToolTipText("Oil intensity");
		intensity.addChangeListener(this);
		intensity.setValue((int)Point.MAX_OIL);

		addWind = new JButton("Add wind");
		addWind.setActionCommand("addWind");
		addWind.addActionListener(this);

		addCurrent = new JButton("Add ocean current");
		addCurrent.setActionCommand("addCurrent");
		addCurrent.addActionListener(this);

		saveBtn = new JButton("Save board to file");
		saveBtn.setActionCommand("saveBtn");
		saveBtn.addActionListener(this);

		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonPanel.add(pred);
		buttonPanel.add(intensity);
		buttonPanel.add(addWind);
		buttonPanel.add(addCurrent);
		buttonPanel.add(saveBtn);

		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
		stats = new Statistics(statsPanel);

		/* poniższe rzutowanie jest takie zjebane po to żeby nie musieć przerabiać nieswojego kodu, nie bijcie */
		if(serializedBoard == null) {
			board = new Board(((Program) frame).getImage());
		}
		else {
			board = IOHelper.fromFile(serializedBoard);
		}

		board.setStats(stats);
		container.add(board, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		container.add(statsPanel, BorderLayout.EAST);
		statsPanel.setPreferredSize(new Dimension(400, frame.getHeight()));
		board.repaint();
	}

	/**
	 * handles clicking on each button
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			iterNum++;
			frame.setTitle(Program.WINDOW_NAME + " (" + Integer.toString(iterNum) + " iteration)");
			board.iteration();
		} else {
			String command = e.getActionCommand();
			if (command.equals("Start")) {
				if (!running) {
					timer.start();
					start.setText("Pause");
				} else {
					timer.stop();
					start.setText("Start");
				}
				running = !running;
				clear.setEnabled(true);

			}
			else if (command.equals("clear")) {
				iterNum = 0;
				timer.stop();
				start.setEnabled(true);
				board.clear();
			}
			else if (command.equals("addWind")) {
				board.setAddingMode(2);
			}
			else if (command.equals("addCurrent")) {
				board.setAddingMode(1);
			}
			else if(command.equals("saveBtn")){
				saveBoard();
			}

		}
	}

	private void saveBoard(){
		JFileChooser fc = new JFileChooser();
		int result = fc.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION){
			IOHelper.toFile(board, fc.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * slider to control simulation speed
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if(e.getSource().equals(pred)) {
			timer.setDelay(maxDelay - pred.getValue());
		}
		else if(e.getSource().equals(intensity)){
			board.setIntensity(intensity.getValue());
		}
	}

	public void setScale(double scale){
		board.setScale(scale);
		stats.setScale(board.getRealPixelExpanse());
	}
}
