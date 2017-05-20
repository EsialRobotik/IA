package fr.esialrobotik;

import com.google.inject.Inject;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.data.table.TableColor;

/**
 * Created by icule on 20/05/17.
 */
public class ColorDetector {
    @Inject
    public ColorDetector(ConfigurationManager configurationManager) {

    }

    public TableColor getSelectedColor() {
        //TODO code it
        return TableColor.BLUE;
    }
}
