package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardCache {
    private BufferedImage image;
    private Board board;
    private int width;
    private int height;


    public BoardCache(Board board, int width, int height){
        this.board = board;
        this.width = width;
        this.height = height;
    }

    public void updateCache(){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics ig = image.createGraphics();
        board.getCacheImage(ig);
    }

    public void getFromCache(Graphics g){
        g.drawImage(image, 0, 0, null);
    }
}
