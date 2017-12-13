package pl.alphabyte.oilSimulator;

import java.awt.image.BufferedImage;
import java.io.*;

public class IOHelper {
    public static Point[][] loadPointsFromImage(BufferedImage img){
        Point[][] result = null;
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        System.out.println(imgHeight + "h    w" + imgWidth);
        result = new Point[imgHeight][imgWidth];

        for(int i=0; i<imgHeight; i++){
            for(int j=0; j<imgWidth; j++){
                int color = img.getRGB(j, i);

                Point p = new Point();
                if(color != Board.IMAGE_WATER_COLOR) {
                    p.setType(1);
                }
                else {
                    p.setType(0);
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
        } catch(IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }

        return board;
    }

}
