package pl.alphabyte.oilSimulator;

/**
 *
 */

public class CurrentCalculator {

    /**
     * calculates line equations for the line constructed through 2 points
     * and two perpendicular lines constructed through each of the points
     *
     * @param x1 the x coordinate of the first point
     * @param x2 the y coordinate of the first point
     * @param y1 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @return array contains 3line equations factors: main line in the first row, both perpendicular lines in the second
     */
    public static double[][] calculateLinesEquationsTab(double x1, double x2, double y1, double y2){
        double[][] paramsTab = new double[2][3]; // tablica wspolczynników 3 prostych
        //wiersz 1 zawiera wspolczynniki A B C prostej wyznaczonej przez wektor w postaci Ax + By + C = 0
        //wiersz 2 zawiera wspolczynniki a2 = a3 oraz b2,b3 prostych prostopadłych do pierwszej

        if(x2 - x1 == 0){
            paramsTab[1][0] = 0;
            if(y2 > y1){
                paramsTab[1][1] = y1;
                paramsTab[1][2] = y2;
            } else {
                paramsTab[1][1] = y2;
                paramsTab[1][2] = y1;
            }
            paramsTab[0][0] = 1;
            paramsTab[0][1] = 0;
            paramsTab[0][2] = -x1;
        }else
        {
            if(y2 - y1 == 0){
                paramsTab[1][0] = 0;
                if(x2 > x1){
                    paramsTab[1][1] = x1;
                    paramsTab[1][2] = x2;
                } else {
                    paramsTab[1][1] = x2;
                    paramsTab[1][2] = x1;
                }
                paramsTab[0][0] = 0;
                paramsTab[0][1] = 1;
                paramsTab[0][2] = -y1;

            }else{
                paramsTab[0][0] = (y2 - y1) / (x2 - x1);
                paramsTab[0][1] = -1;
                paramsTab[0][2] = y1 - paramsTab[0][0] * x1;

                paramsTab[1][0] = -1/paramsTab[0][0];

                if(y2 > y1){
                    paramsTab[1][1] = y1 - paramsTab[1][0] * x1;
                    paramsTab[1][2] = y2 - paramsTab[1][0] * x2;
                } else {
                    paramsTab[1][2] = y1 - paramsTab[1][0] * x1;
                    paramsTab[1][1] = y2 - paramsTab[1][0] * x2;
                }
            }
        }
        return paramsTab;
    }


    /**
     * calculates normalized vector based on two points
     * @param x1 the x coordinate of the first point
     * @param x2 the y coordinate of the first point
     * @param y1 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @return 2D-vector in a array
     */
    public static double[] calculateVector(double x1, double x2, double y1, double y2){
        double [] vector = new double[2];
        double lenght;

        vector[0] = x2 - x1;
        vector[1] = y2 - y1;

        lenght = Math.sqrt(Math.pow(vector[0],2) + Math.pow(vector[1], 2));

        vector[0] = vector[0] / lenght / 3;
        vector[1] = vector[1] / lenght / 3;

        return vector;
    }

    /**
     * calculates distance of a point (i,j) from the line
     * @param i the x coordinate of the point
     * @param j the y coordinate of the point
     * @param linesParamsTab array contains 3line equations factors: main line in the first row, both perpendicular lines in the second
     * @return calculated distance
     */
    public static double calculateDistance(int i, int j, double[][] linesParamsTab){
        //obliczanie odleglosci punktu (i,j) do prostej o parametrach w tablicy
        double dist;
        dist = Math.abs(linesParamsTab[0][0] * i + linesParamsTab[0][1] * j + linesParamsTab[0][2]) /
                Math.sqrt( Math.pow(linesParamsTab[0][0],2) + Math.pow(linesParamsTab[0][1],2));
        return dist;
    }

    /**
     * test if the point (i,j) is located within certain distance from the line constrained by two perpendicular lines
     * @param dist max distance from the main line
     * @param linesParamsTab array contains 3line equations factors: main line in the first row, both perpendicular lines in the second
     * @param i the x coordinate of the point
     * @param j the y coordinate of the point
     * @param vector 2D-vector in a array
     * @return true if the point is located within said area
     */
    public static boolean isPointNearCurrent(double dist, double[][] linesParamsTab, int i, int j, double[] vector){
        //testowanie czy dany punkt znajduje się w sąsiedztwie wektora
        boolean val;
        val =   (dist < OceanCurrent.currRange && j < linesParamsTab[1][0] * i + linesParamsTab[1][2] && j > linesParamsTab[1][0] * i + linesParamsTab[1][1]) ||
                (vector[1] == 0 && dist < OceanCurrent.currRange && i < linesParamsTab[1][2] && i > linesParamsTab[1][1]);
        return val;
    }

}
