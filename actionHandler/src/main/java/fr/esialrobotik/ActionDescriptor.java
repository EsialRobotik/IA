package fr.esialrobotik;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class ActionDescriptor {
  private int objectiveId;
  private int points;
  private int time;

  private List<Step> stepList;

  public ActionDescriptor(JsonObject object) {
    stepList = new ArrayList<Step>();

    objectiveId = object.get("id").getAsInt();
    points = object.get("points").getAsInt();
    time = object.get("temps").getAsInt();

    for(JsonElement elt : object.get("taches").getAsJsonArray()) {
      stepList.add(new Step(elt.getAsJsonObject()));
    }
  }

  public String toString() {
    String res = "id : " + objectiveId;
    res += "\npoints: " + points;
    res += "\ntemps : " + time;
    for(Step s : stepList) {
      res += "\n" + s.toString();
    }
    return res;
  }

  public static void main(String[] args) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    JsonElement elt = parser.parse(new JsonReader(new FileReader("actionHandler/configAction.json")));

    ActionDescriptor actionDescriptor = new ActionDescriptor(elt.getAsJsonObject());
    System.out.println(actionDescriptor);
  }

}
