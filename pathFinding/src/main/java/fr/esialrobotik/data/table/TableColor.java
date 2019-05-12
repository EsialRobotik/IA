package fr.esialrobotik.data.table;

/**
 * Created by icule on 28/03/17.
 */
public enum TableColor {
    COLOR_0("Jaune"),
    COLOR_3000("Violet");

    private String colorName;

    TableColor(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String toString() {
        return colorName;
    }
}
