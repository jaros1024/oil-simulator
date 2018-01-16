package pl.alphabyte.oilSimulator;

import javax.swing.*;
import static java.lang.String.format;

/**
 * This class represents the statistics panel that shows on GUI, right to the board.
 * All its methods are setters.
 */

public class Statistics {
    private static final String SCALE_TEXT = "Scale: 1 px = %.2f km^2";
    private static final String DAYS_TEXT = "Days: %d";
    private static final String CURR_CONT_AREA = "Currently contaminated area: %.2f km^2";
    private static final String ALL_CONT_AREA = "All contaminated area: %.2f km^2";
    private static final String VOLUME = "Oil volume: %.2f m^3";

    private JLabel scaleLabel;
    private JLabel daysLabel;
    private JLabel currentExpanseLabel;
    private JLabel allExpanseLabel;
    private JLabel volumeLabel;

    public Statistics(JPanel rootPanel){
        scaleLabel = new JLabel();
        daysLabel = new JLabel();
        currentExpanseLabel = new JLabel();
        allExpanseLabel = new JLabel();
        volumeLabel = new JLabel();

        rootPanel.add(scaleLabel);
        rootPanel.add(daysLabel);
        rootPanel.add(currentExpanseLabel);
        rootPanel.add(allExpanseLabel);
        rootPanel.add(volumeLabel);

        setScale(0);
        setDays(0);
        setCurrentExpanse(0);
        setAllExpanse(0);
        setVolume(0);
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

    public void setVolume(double volume) {
        volumeLabel.setText(format(VOLUME, volume));
    }

}
