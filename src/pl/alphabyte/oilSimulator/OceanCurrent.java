package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class OceanCurrent {
    private Vector<java.awt.Point> points = new Vector<java.awt.Point>();
    private boolean finished = false;
    private java.awt.Point cursorPosition;

    private int addingMode = 0;

    public void draw(Graphics g){
        g.setColor(Color.BLACK);
        int x1, x2, y1, y2;

        for(int i=0; i<points.size()-1; i++){
            x1 = points.elementAt(i).x;
            y1 = points.elementAt(i).y;
            x2 = points.elementAt(i+1).x;
            y2 = points.elementAt(i+1).y;

            g.drawLine(x1, y1, x2, y2);
        }

        if(!finished) {
            if (points.size() > 0) {
                g.drawLine(points.lastElement().x, points.lastElement().y, cursorPosition.x, cursorPosition.y);
            }
        }
    }

    public void addNextPoint(java.awt.Point cursorPosition){
        points.add(cursorPosition);
    }

    public void setCursorPosition(Point cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public void setFinished() {
        this.finished = true;
    }

    public static class Factory {

        private OceanCurrent tmpCurrent;
        private Board board;

        public Factory(Board board){
            this.board = board;
        }

        public void createCurrent(java.awt.Point p){
            tmpCurrent = new OceanCurrent();
            tmpCurrent.addNextPoint(p);
            tmpCurrent.setCursorPosition(p);
            board.repaint();
        }

        public OceanCurrent saveCurrent(){
            tmpCurrent.setFinished();
            OceanCurrent returnValue = tmpCurrent;
            tmpCurrent = null;
            board.setAddingMode(0);
            board.repaint();
            return returnValue;
        }

        public void handleMouseClick(MouseEvent e){
            if(!isBeingCreated()){
                createCurrent(e.getPoint());
            }
            else {
                addNextPoint(e.getPoint());
            }
        }

        public void handleMouseMove(MouseEvent e){
            setCursorPosition(e.getPoint());
            board.repaint();
        }

        public void addNextPoint(java.awt.Point point){
            tmpCurrent.addNextPoint(point);
        }

        public boolean isBeingCreated(){
            return (tmpCurrent != null);
        }

        public OceanCurrent getTmpCurrent(){
            return tmpCurrent;
        }

        private void setCursorPosition(java.awt.Point position){
            tmpCurrent.setCursorPosition(position);
        }
    }
}
