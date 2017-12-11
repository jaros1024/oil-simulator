package pl.alphabyte.oilSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Wind {
    private final int x0, y0;
    private int x, y;
    private int width = 0, height = 0;
    private Double speed;
    private Double direction;


    public Wind(int x, int y){
        this.x = this.x0 = x;
        this.y = this.y0 = y;
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
        }

        public Wind saveWind(java.awt.Point p){
            tmpWind.setNewSize(p);
            board.setAddingMode(0);

            while(!tmpWind.setDirection());
            while (!tmpWind.setSpeed());

            Wind returnValue = tmpWind;
            tmpWind = null;
            board.repaint();
            return returnValue;
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
