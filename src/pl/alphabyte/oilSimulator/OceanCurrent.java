package pl.alphabyte.oilSimulator;

import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class represents single ocean current, with factory included.
 */
public class OceanCurrent implements Serializable {
    private Vector<java.awt.Point> points = new Vector<java.awt.Point>();
    private boolean finished = false;
    private java.awt.Point cursorPosition;
    public static final double currRange = 20;
    public static final double currSpeed = 20;

    /**
     * Draws single ocean current
     * @param g @see java.awt.Graphics
     */
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

    /**
     * Adds next point to the current
     * @param cursorPosition A position of the next point
     */
    public void addNextPoint(java.awt.Point cursorPosition){
        points.add(cursorPosition);
    }

    /**
     * Sets the current cursor position, so that the unfinished
     * current could follow the cursor
     * @param cursorPosition A position of the cursor
     */
    public void setCursorPosition(Point cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public void setFinished() {
        this.finished = true;
    }

    /**
     * Helper class that manages current creating
     */
    public static class Factory {
        private OceanCurrent tmpCurrent;
        private Board board;

        public Factory(Board board){
            this.board = board;
        }

        /**
         * Starts the process of creating current
         * @param p A point where the current is starting
         */
        public void createCurrent(java.awt.Point p){
            tmpCurrent = new OceanCurrent();
            tmpCurrent.addNextPoint(p);
            tmpCurrent.setCursorPosition(p);
            board.repaint();
        }

        /**
         * Finishes the current creating process
         * @return Newly created current
         */
        public OceanCurrent saveCurrent(){
            tmpCurrent.setFinished();
            applyCurrent();
            OceanCurrent returnValue = tmpCurrent;
            tmpCurrent = null;
            board.setAddingMode(0);
            board.repaint();
            return returnValue;
        }

        /**
         * Handles single mouse click when the current is being created
         * @param e Mouse event
         */
        public void handleMouseClick(MouseEvent e){
            if(!isBeingCreated()){
                createCurrent(e.getPoint());
            }
            else {
                addNextPoint(e.getPoint());
            }
        }

        /**
         * Handles mouse move when the current is being created
         * @param e Mouse event
         */
        public void handleMouseMove(MouseEvent e){
            setCursorPosition(e.getPoint());
            board.repaint();
        }

        /**
         * Applies the currently created current
         */
        public void applyCurrent() { applyCurrent(tmpCurrent);}

        /**
         * Applies the given current
         * @param tmpCurrent Ocean current to apply
         */
        public void applyCurrent(OceanCurrent tmpCurrent){
            Vector<Point> currPoints = tmpCurrent.getPoints();
            double x1,x2,y1,y2;
            double[][] linesParamsTab;
            double dist;
            double[] pointParamsTab = {0,0,0,0};
            double[] vector;
            pl.alphabyte.oilSimulator.Point point;
            double distFactor = 20;

            for (int id = 0; id < currPoints.size() - 1; id++ ){
                x1 = currPoints.get(id).getX();
                y1 = currPoints.get(id).getY();
                x2 = currPoints.get(id +1).getX();
                y2 = currPoints.get(id +1).getY();

                if(x1 == x2 && y1 == y2) continue;

                linesParamsTab = CurrentCalculator.calculateLinesEquationsTab(x1,x2,y1,y2);
                vector = CurrentCalculator.calculateVector(x1,x2,y1,y2);

                for(int i = 0; i < board.getXSize(); i++){
                    for (int j = 0; j < board.getYSize(); j++){

                        dist = CurrentCalculator.calculateDistance(i,j,linesParamsTab);

                        if(CurrentCalculator.isPointNearCurrent(dist, linesParamsTab, i, j, vector))
                        {
                            for(int it = 0; it < pointParamsTab.length; it++) pointParamsTab[it] = 0;

                            if (vector[0] >= 0) pointParamsTab[1] = vector[0] * tmpCurrent.currSpeed / Math.log(dist + distFactor);
                            else pointParamsTab[2] = -1 * vector[0] * tmpCurrent.currSpeed / Math.log(dist + distFactor);

                            if (vector[1] >= 0) pointParamsTab[0] = vector[1] * tmpCurrent.currSpeed / Math.log(dist + distFactor);
                            else pointParamsTab[3] = -1 * vector[1] * tmpCurrent.currSpeed / Math.log(dist + distFactor);

                            point = board.getPoint(i, j);
                            point.modifyCalculationParams(pointParamsTab);
                            board.setPoint(i, j, point);
                        }
                    }
                }
            }
        }

        /**
         * Adds next point to the currently created ocean current
         * @param point A position of the next point
         */
        public void addNextPoint(java.awt.Point point){
            tmpCurrent.addNextPoint(point);
        }

        /**
         * Checks if a current is being created
         * @return true or false
         */
        public boolean isBeingCreated(){
            return (tmpCurrent != null);
        }

        public OceanCurrent getTmpCurrent(){
            return tmpCurrent;
        }

        /**
         * Sets the current cursor position, so that the unfinished
         * current could follow the cursor
         * @param position A position of the cursor
         */
        private void setCursorPosition(java.awt.Point position){
            tmpCurrent.setCursorPosition(position);
        }
    }
}
