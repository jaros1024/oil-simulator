package pl.alphabyte.oilSimulator;

public class CurrentFactory {
    private static OceanCurrent tmp;

    public static void startCurrent(){
        tmp = new OceanCurrent();
    }

    public static OceanCurrent endCurrent(){
        return tmp;
    }
}
