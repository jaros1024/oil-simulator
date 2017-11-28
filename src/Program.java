import javax.swing.*;

public class Program extends JFrame {

	public static final String WINDOW_NAME = "Oil Simulator";
	private GUI gui;

	public Program() {
		setTitle(WINDOW_NAME);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		gui = new GUI(this);
		gui.initialize(this.getContentPane());

		this.setSize(1024, 768);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Program();
	}

}
