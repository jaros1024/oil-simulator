package pl.alphabyte.oilSimulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitialWindow {
    public JPanel panel1;
    private JButton loadBoardFromFileButton;
    private JButton designNewBoardButton;

    private Program program;
    private String fileToLoad = null;

    public InitialWindow(Program program){
        this.program = program;
    }

    public void initialize(){
        loadBoardFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBoardFromFile();
                synchronized (program.lock) {
                    program.lock.notify();
                }
            }
        });

        designNewBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (program.lock) {
                    program.lock.notify();
                }
            }
        });
    }

    private void loadBoardFromFile(){
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(panel1);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileToLoad = fc.getSelectedFile().getAbsolutePath();
        }
    }

    public String getFileToLoad() {
        return fileToLoad;
    }
}
