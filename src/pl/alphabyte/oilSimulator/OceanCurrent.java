package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Vector;

public class OceanCurrent implements Serializable {
    private Vector<java.awt.Point> points = new Vector<java.awt.Point>();
    private boolean finished = false;
    private java.awt.Point cursorPosition;

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


    public Vector<java.awt.Point> getPoints(){
        return points;
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
        private static final double CURR_SPEED = 4;
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
            applyCurrent();
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

        public void applyCurrent(){
            Vector<java.awt.Point> currPoints = tmpCurrent.getPoints();
            double x1,x2,y1,y2;
            double a1,b1,c1;
            double a2,b2,b3;
            int maxDist = 10;
            double dist;
            double[] paramsTab = {0,0,0,0};
            double vecX, vecY, vMax;
            pl.alphabyte.oilSimulator.Point point;
            for (int id = 0; id < currPoints.size() - 1; id++ ){

                x1 = currPoints.get(id).getX();
                y1 = currPoints.get(id).getY();
                x2 = currPoints.get(id +1).getX();
                y2 = currPoints.get(id +1).getY();

                if(x1 == x2 && y1 == y2) continue;

                if(x2 - x1 == 0){
                    a2 = 0;
                    if(y2 > y1){
                        b2 = y1;
                        b3 = y2;
                    } else {
                        b2 = y2;
                        b3 = y1;
                    }
                    a1 = 1;
                    b1 = 0;
                    c1 = -x1;
                }else
                {
                    if(y2 - y1 == 0){
                        a2 = 0;
                        if(x2 > x1){
                            b2 = x1;
                            b3 = x2;
                        } else {
                            b2 = x2;
                            b3 = x1;
                        }
                        a1 = 0;
                        b1 = 1;
                        c1 = -y1;

                    }else{
                        a1 = (y2 - y1) / (x2 - x1);
                        b1 = -1;
                        c1 = y1 - a1 * x1;

                        a2 = -1/a1;

                        if(y2 > y1){
                            b2 = y1 - a2 * x1;
                            b3 = y2 - a2 * x2;
                        } else {
                            b3 = y1 - a2 * x1;
                            b2 = y2 - a2 * x2;
                        }
                    }
                }

                vecX = x2 - x1;
                vecY = y2 - y1;
                if(Math.abs(vecX) > Math.abs(vecY)){
                    vMax = Math.abs(vecX);
                    vecX = vecX / (Math.abs(vMax)+1);
                    vecY = vecY / (Math.abs(vMax)+1);
                } else {
                    vMax = Math.abs(vecY);
                    vecX = vecX / (Math.abs(vMax)+1);
                    vecY = vecY / (Math.abs(vMax)+1);
                }
                for(int i = 0; i < board.getXSize(); i++){
                    for (int j = 0; j < board.getYSize(); j++){

                        for(int it = 0; it < paramsTab.length; it++) paramsTab[it] = 0;

                        dist = Math.abs(a1 * i + b1 * j + c1) / Math.sqrt(a1 * a1 + b1 * b1);

                        if((dist < maxDist && j < a2 * i + b3 && j > a2 * i + b2) ||
                                (y1 == y2 && dist < maxDist && i < b3 && i > b2)) {
                            if (vecX >= 0) paramsTab[1] = vecX * CURR_SPEED / Math.log(dist + 4);
                            else paramsTab[2] = -1 * vecX * CURR_SPEED / Math.log(dist + 4);

                            if (vecY >= 0) paramsTab[0] = vecY * CURR_SPEED / Math.log(dist + 4);
                            else paramsTab[3] = -1 * vecY * CURR_SPEED / Math.log(dist + 4);

                            point = board.getPoint(i, j);
                            point.modifyCalculationParams(paramsTab);
                            board.setPoint(i, j, point);

                        }


                    }
                }


            }


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
