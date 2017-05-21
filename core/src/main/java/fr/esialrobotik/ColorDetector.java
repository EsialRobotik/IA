package fr.esialrobotik;

import com.google.inject.Inject;
import esialrobotik.ia.utils.gpio.raspberry.GpioInput;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.data.table.TableColor;

/**
 * Created by icule on 20/05/17.
 */
public class ColorDetector {

    private GpioInput colorDetector;

    @Inject
    public ColorDetector(ConfigurationManager configurationManager) {
        this.colorDetector = new GpioInput(configurationManager.getColorGpio(), false);
    }

    public TableColor getSelectedColor() {
        return this.colorDetector.isLow() ? TableColor.BLUE : TableColor.RED;
    }

}
