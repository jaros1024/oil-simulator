package pl.alphabyte.oilSimulator;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class contains the frame with map selector
 */
public class MapSelector {
    private static final double ZOOM_CONST = 0.05;

    private Program program;
    public JPanel panel1;
    private JButton loadImageButton;
    private JButton selectButton;
    private JLabel label;
    private JPanel centerPanel;
    private JScrollPane scrollPane;
    private JButton zoomInButton;
    private JButton zoomOutButton;

    private BufferedImage fullImage;
    private BufferedImage scaledImage;

    private double scale;


    public MapSelector(Program program){
        this.program = program;
    }

    /**
     * Initializing of the frame
     */
    public void initialize(){
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileLoaded()){
                    return;
                }
                selectImage();
            }
        });

        /**
         * Load image action listener
         */
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        /**
         * Zoom in action listener
         */
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileLoaded()){
                    return;
                }
                scale += ZOOM_CONST;
                if(scale > 1){
                    scale = 1;
                }
                if(scale == 1) {
                    ImageIcon icon = new ImageIcon(fullImage);
                    label.setIcon(icon);
                }
                else {
                    scaleImage(scale);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    label.setIcon(icon);
                }
            }
        });

        /**
         * Zoom out action listener
         */
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!fileLoaded()){
                    return;
                }
                scale -= ZOOM_CONST;
                if(scale < 0.07){
                    scale = 0.07;
                }
                scaleImage(scale);
                ImageIcon icon = new ImageIcon(scaledImage);
                label.setIcon(icon);
            }
        });
    }

    /**
     * Selecting currently visible part of the full map
     */
    private void selectImage(){
        if(scale == 1){
            scaledImage = fullImage;
        }
        BufferedImage trimmedImage = scaledImage.getSubimage(getX(), getY(), getWidth(), getHeight());
        synchronized (program.lock){
            program.setImage(trimmedImage);
            program.lock.notify();
        }
    }

    /**
     * Loading file with the map
     */
    private void loadFile(){
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(panel1);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadImage(file);
        }
    }

    /**
     * Reading the map from given file and setting as label icon
     * @param file File containing the map
     */
    private void loadImage(File file){
        fullImage = null;
        try {
            fullImage = ImageIO.read(file);
            scaleImage(0.07);
            ImageIcon icon = new ImageIcon(scaledImage);
            label.setIcon(icon);
            label.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scaling the image
     * @param scale Scale of the map
     */
    private void scaleImage(double scale){
        this.scale = scale;
        int targetSize = (int)Math.round(fullImage.getWidth()*scale);
        scaledImage = Scalr.resize(fullImage, Scalr.Method.AUTOMATIC, targetSize);
    }

    /**
     * Checks if image is loaded and showing the error
     * @return
     */
    private boolean fileLoaded(){
        if(fullImage == null){
            JOptionPane.showMessageDialog(program, "You need to load image file first!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
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

    public double getScale() {
        return scale;
    }
}
