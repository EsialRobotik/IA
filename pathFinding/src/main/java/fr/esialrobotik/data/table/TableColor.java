package fr.esialrobotik.data.table;

/**
 * Created by icule on 28/03/17.
 */
public enum TableColor {
    RED("Rouge"),
    BLUE("Bleu");

    private String configName;
    TableColor(String configName){
        this.configName = configName;
    }

    public static TableColor getTableColorFromConfigName(String configName){
        for(TableColor tc : TableColor.values()){
            if(tc.configName.equals(configName)){
                return tc;
            }
        }
        return null;
    }
}
