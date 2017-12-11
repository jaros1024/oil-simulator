package pl.alphabyte.oilSimulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapSelector {
    public JPanel panel1;
    private JButton loadImageButton;
    private JButton selectButton;
    private JLabel label;
    private JPanel centerPanel;
    private JScrollPane scrollPane;
    private static final String IMG_FILE = "map2.bmp";
    private BufferedImage fullImage;
    private BufferedImage trimmedImage;
    private Program program;

    public MapSelector(Program program){
        this.program = program;
    }

    public void initialize(){
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

    }

    private void selectImage(){
        BufferedImage trimmedImage = fullImage.getSubimage(getX(), getY(), getWidth(), getHeight());
        synchronized (program.lock){
            program.setImage(trimmedImage);
            program.lock.notify();
        }
    }

    private void loadFile(){
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(panel1);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadImage(file);
        }
    }

    private void loadImage(File file){
        fullImage = null;
        try {
            fullImage = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(fullImage);
            label.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getX(){
        return (int)scrollPane.getViewport().getViewPosition().getX();
    }

    private int getY(){
        return (int)scrollPane.getViewport().getViewPosition().getY();
    }

    private int getWidth(){
        return (int)scrollPane.getViewport().getWidth();
    }

    private int getHeight(){
        return (int)scrollPane.getViewport().getHeight();
    }

}
