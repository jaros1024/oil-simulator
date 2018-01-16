package pl.alphabyte.oilSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * This class represents single wind, with factory included.
 */

public class Wind implements Serializable {
    private final int x0, y0;
    private int x, y;
    private int width = 0, height = 0;
    private Double speed;
    private Double direction;
    private double[] windValueArray;

    public Wind(int x, int y){
        this.x = this.x0 = x;
        this.y = this.y0 = y;
        windValueArray = new double[4];
    }

    public double[] getWindValueArray() {
        return windValueArray;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    /**
     * Calculates the wind array
     */
    public void calculateWindArray(){

        double directionInRadians, valX, valY;
        for (int i = 0; i < windValueArray.length; i++){ windValueArray[i] = 0;}
        double windPowerParam = 0.08;

        directionInRadians = direction * Math.PI / 180;
        valX = Math.cos(directionInRadians)*speed*windPowerParam;
        valY = Math.sin(directionInRadians)*speed*windPowerParam;

        if(valX >= 0) windValueArray[1] = valX;
        else windValueArray[2] = valX*(-1);

        if(valY >= 0) windValueArray[3] = valY;
        else windValueArray[0] = valY*(-1);
    }

    /**
     * Draw the wind on the board
     *
     * @param g @see java.awt.Graphics
     */
    public void draw(Graphics g){
        g.setColor(Color.MAGENTA);

        g.drawRect(x, y, width, height);
    }

    /**
     * Sets new size, based on current mouse position (as {@link java.awt.Point})
     * @param point Current mouse position
     */
    public void setNewSize(java.awt.Point point){
        if(point.x < this.x0) {
            this.x = point.x;
        }

        if(point.y < this.y0) {
            this.y = point.y;
        }

        this.width = Math.abs(x0-point.x);
        this.height = Math.abs(y0-point.y);
    }

    /**
     * Gets wind speed from the user and set it to te wind
     * @return true on success, false on failure
     */
    public boolean setSpeed() {
        String speedStr = (String) JOptionPane.showInputDialog("Please input wind speed:");
        if(speedStr == null){
            return false;
        }

        speed = Double.parseDouble(speedStr);
        if(speed < 0) {
            return false;
        }
        return true;
    }

    /**
     * Gets wind direction from the user and set it to te wind
     * @return true on success, false on failure
     */
    public boolean setDirection() {
        String directionStr = JOptionPane.showInputDialog("Please input wind direction (0-360):");
        if(directionStr == null){
            return false;
        }

        direction = Double.parseDouble(directionStr);
        if(direction < 0 || direction > 360){
            return false;
        }
        return true;
    }

    /**
     * Helper class that manages wind creating
     */
    public static class Factory {
        private Wind tmpWind;
        private Board board;

        public Factory(Board board){
            this.board = board;
        }

        public void createWind(java.awt.Point p){
            tmpWind = new Wind(p.x, p.y);
            board.repaint();
        }

        /**
         * Finalizes the wind creating process and returns currently created wind
         * @param p Position where the wind should be finally created
         * @return Newly created wind
         */
        public Wind saveWind(java.awt.Point p){
            tmpWind.setNewSize(p);
            board.setAddingMode(0);

            while(!tmpWind.setDirection());
            while (!tmpWind.setSpeed());

            tmpWind.calculateWindArray();
            applyWind();
            Wind returnValue = tmpWind;
            tmpWind = null;
            board.repaint();
            return returnValue;
        }

        /**
         * Applies currently created wind
         */
        public void applyWind(){
            applyWind(tmpWind);
        }

        /**
         * Applies given wind
         * @param tmpWind A wind to apply
         */
        public void applyWind(Wind tmpWind){
            Point tmpPoint;

            for (int i = tmpWind.getX(); i < tmpWind.getX() + tmpWind.getWidth(); i++){
                for (int j = tmpWind.getY(); j < tmpWind.getY() + tmpWind.getHeight(); j++){
                    tmpPoint = board.getPoint(i,j);
                    tmpPoint.modifyCalculationParams(tmpWind.getWindValueArray());
                    board.setPoint(i,j,tmpPoint);
                }
            }
        }

        /**
         * Changes a size of wind, basing on mouse position (as {@link java.awt.Point} )
         * @param p
         */
        public void updateSize(java.awt.Point p){
            tmpWind.setNewSize(p);
            board.repaint();
        }

        /**
         * Handles mouse dragged event when creating wind
         * @param e Mouse event
         */
        public void handleMouseDragged(MouseEvent e){
            if(!isBeingCreated()){
                createWind(e.getPoint());
            }
            else {
                updateSize(e.getPoint());
            }
        }

        /**
         * Checks if any wind is being created right now
         * @return true or false
         */
        public boolean isBeingCreated() { return (tmpWind != null); }

        /**
         * Returns currently created wind
         * @return Wind
         */
        public Wind getTmpWind() { return tmpWind; }
    }
}
