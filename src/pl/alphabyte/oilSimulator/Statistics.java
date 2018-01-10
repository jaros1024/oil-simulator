package pl.alphabyte.oilSimulator;

import javax.swing.*;
import static java.lang.String.format;

public class Statistics {
    private static final String SCALE_TEXT = "Scale: 1 px = %.2f km^2";
    private static final String DAYS_TEXT = "Days: %d";
    private static final String CURR_CONT_AREA = "Currently contaminated area: %.2f km^2";
    private static final String ALL_CONT_AREA = "All contaminated area: %.2f km^2";

    /*private JPanel rootPanel;*/
    private JLabel scaleLabel;
    private JLabel daysLabel;
    private JLabel currentExpanseLabel;
    private JLabel allExpanseLabel;

    public Statistics(JPanel rootPanel){
        /*this.rootPanel = rootPanel;*/

        scaleLabel = new JLabel();
        daysLabel = new JLabel();
        currentExpanseLabel = new JLabel();
        allExpanseLabel = new JLabel();

        rootPanel.add(scaleLabel);
        rootPanel.add(daysLabel);
        rootPanel.add(currentExpanseLabel);
        rootPanel.add(allExpanseLabel);

        setScale(0);
        setDays(0);
        setCurrentExpanse(0);
        setAllExpanse(0);
    }

    public void setScale(double scale){
        scaleLabel.setText(format(SCALE_TEXT, scale));
    }

    public void setDays(int days){
        daysLabel.setText(format(DAYS_TEXT, days));
    }

    public void setCurrentExpanse(double expanse){
        currentExpanseLabel.setText(format(CURR_CONT_AREA, expanse));
    }

    public void setAllExpanse(double expanse){
        allExpanseLabel.setText(format(ALL_CONT_AREA, expanse));
    }


}
