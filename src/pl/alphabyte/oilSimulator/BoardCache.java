package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class is used as a cache of board.
 * It prevents lagging when adding ocean currents or winds.
 */

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

    /**
     * Updates board image stores in cache
     */
    public void updateCache(){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics ig = image.createGraphics();
        board.getCacheImage(ig);
    }

    /**
     * Draws cached board
     * @param g @see java.awt.Graphics
     */
    public void getFromCache(Graphics g){
        g.drawImage(image, 0, 0, null);
    }
}
