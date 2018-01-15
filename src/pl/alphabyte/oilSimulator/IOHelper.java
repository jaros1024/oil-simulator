package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class IOHelper {
    public static final int WATER_THRESHOLD = 245;

    public static Point[][] loadPointsFromImage(BufferedImage img){
        Point[][] result = null;
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        result = new Point[imgHeight][imgWidth];

        for(int i=0; i<imgHeight; i++){
            for(int j=0; j<imgWidth; j++){
                Color c = new Color(img.getRGB(j, i));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                Point p = new Point();

                if(red > WATER_THRESHOLD && green > WATER_THRESHOLD && blue > WATER_THRESHOLD){
                    p.setType(0);
                }
                else {
                    p.setType(1);
                }
                result[i][j] = p;
            }
        }
        return result;
    }

    public static void toFile(Board board, String file){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(board);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Board fromFile(String file){
        Board board = null;
        try {
            FileInputStream fileIn =
                    new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            board = (Board) in.readObject();
            in.close();
            fileIn.close();
            board.setIntensity(Point.MAX_OIL);
        } catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }

        return board;
    }

}
