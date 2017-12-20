package pl.alphabyte.oilSimulator;
public class CurrentCalculator {

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

    public static double[] calculateVector(double x1, double x2, double y1, double y2){

        // sprowadzenie wektora do przedziału <0, 1>

        double [] vector = new double[2];
        double vMax;
        vector[0] = x2 - x1;
        vector[1] = y2 - y1;
        if(Math.abs(vector[0]) > Math.abs(vector[1])){
            vMax = Math.abs(vector[0]);
            vector[0] = vector[0] / (Math.abs(vMax)+1);
            vector[1] = vector[1] / (Math.abs(vMax)+1);
        } else {
            vMax = Math.abs(vector[1]);
            vector[0] = vector[0] / (Math.abs(vMax)+1);
            vector[1] = vector[1] / (Math.abs(vMax)+1);
        }
        return vector;
    }
    public static double calculateDistance(int i, int j, double[][] linesParamsTab){
        //obliczanie odleglosci punktu (i,j) do prostej o parametrach w tablicy
        double dist;
        dist = Math.abs(linesParamsTab[0][0] * i + linesParamsTab[0][1] * j + linesParamsTab[0][2]) /
                Math.sqrt( Math.pow(linesParamsTab[0][0],2) + Math.pow(linesParamsTab[0][1],2));
        return dist;
    }

    public static boolean isPointNearCurrent(double dist, double[][] linesParamsTab, int i, int j, double[] vector){
        //testowanie rozległym warunkiem, czy dany punkt znajduje się w sąsiedztwie wektora
        boolean val;
        val =   (dist < OceanCurrent.currRange && j < linesParamsTab[1][0] * i + linesParamsTab[1][2] && j > linesParamsTab[1][0] * i + linesParamsTab[1][1]) ||
                (vector[1] == 0 && dist < OceanCurrent.currRange && i < linesParamsTab[1][2] && i > linesParamsTab[1][1]);
        return val;
    }

}
