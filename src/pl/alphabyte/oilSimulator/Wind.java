package pl.alphabyte.oilSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;

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


    public void calculateWindArray(){

        double directionInRadians, valX, valY;
        for (int i = 0; i < windValueArray.length; i++){ windValueArray[i] = 0;}
        double windPowerParam = 0.1;

        directionInRadians = direction / 2 / Math.PI;
        valX = speed*Math.cos(directionInRadians)*speed*windPowerParam;
        valY = speed*Math.sin(directionInRadians)*speed*windPowerParam;

        if(valX >= 0) windValueArray[1] = valX;
        else windValueArray[2] = valX*(-1);

        if(valY >= 0) windValueArray[3] = valY;
        else windValueArray[0] = valY*(-1);
    }


    public void draw(Graphics g){
        g.setColor(Color.MAGENTA);

        g.drawRect(x, y, width, height);
    }

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

    public boolean setSpeed() {
        String speedStr = (String) JOptionPane.showInputDialog("Please input wind speed:");

        speed = Double.parseDouble(speedStr);
        if(speed < 0) {
            return false;
        }
        return true;
    }

    public boolean setDirection() {
        String directionStr = JOptionPane.showInputDialog("Please input wind direction (0-360):");

        direction = Double.parseDouble(directionStr);
        if(direction < 0 || direction > 360){
            return false;
        }
        return true;
    }

    public static class Factory {
        private Wind tmpWind;
        private Board board;

        public Factory(Board board){
            this.board = board;
        }

        public void createWind(java.awt.Point p){
            tmpWind = new Wind(p.x, p.y);
            board.repaint();
            System.out.println(p.x + " px py " + p.y);
        }

        public Wind saveWind(java.awt.Point p){
            tmpWind.setNewSize(p);
            board.setAddingMode(0);

            while(!tmpWind.setDirection());
            while (!tmpWind.setSpeed());

            tmpWind.calculateWindArray();
            System.out.println(tmpWind.getX() + "   " + tmpWind.getWidth() + "     " + tmpWind.getY() + "       " + tmpWind.getHeight());
            applyWind();
            Wind returnValue = tmpWind;
            tmpWind = null;
            board.repaint();
            return returnValue;
        }

        public void applyWind(){
            Point tmpPoint;

            for (int i = tmpWind.getX(); i < tmpWind.getX() + tmpWind.getWidth(); i++){
                for (int j = tmpWind.getY(); j < tmpWind.getY() + tmpWind.getHeight(); j++){
                    tmpPoint = board.getPoint(i,j);
                    tmpPoint.modifyCalculateParams(tmpWind.getWindValueArray());
                    board.setPoint(i,j,tmpPoint);
                    System.out.println();
                    System.out.println(i + "  " + j);
                }
            }


        }

        public void updateSize(java.awt.Point p){
            tmpWind.setNewSize(p);
            board.repaint();
        }

        public void handleMouseDragged(MouseEvent e){
            if(!isBeingCreated()){
                createWind(e.getPoint());
            }
            else {
                updateSize(e.getPoint());
            }
        }

        public boolean isBeingCreated() { return (tmpWind != null); }

        public Wind getTmpWind() { return tmpWind; }
    }
}
