package fr.esialrobotik.data.table;

/**
 * Created by icule on 28/03/17.
 */
public enum TableColor {
    COLOR_0("Vert"),
    COLOR_3000("Orange");

    private String colorName;

    TableColor(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String toString() {
        return colorName;
    }
}
