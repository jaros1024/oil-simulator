package pl.alphabyte.oilSimulator;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapSelector {
    private static final String IMG_FILE = "map2.bmp";
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
    private BufferedImage trimmedImage;

    private double scale;


    public MapSelector(Program program){
        this.program = program;
    }

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

        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });
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
            scaleImage(0.07);
            ImageIcon icon = new ImageIcon(scaledImage);
            label.setIcon(icon);
            label.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scaleImage(double scale){
        this.scale = scale;
        int targetSize = (int)Math.round(fullImage.getWidth()*scale);
        scaledImage = Scalr.resize(fullImage, Scalr.Method.AUTOMATIC, targetSize);
    }

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
