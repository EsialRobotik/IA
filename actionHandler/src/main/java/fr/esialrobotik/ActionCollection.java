package fr.esialrobotik;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class ActionCollection {
  private List<ActionDescriptor> actionList;

  @Inject
  public ActionCollection(JsonArray configurationNode) {
    actionList = new ArrayList<ActionDescriptor>();

    for(JsonElement element : configurationNode) {
      actionList.add(new ActionDescriptor(element.getAsJsonObject()));
    }
  }

  public String toString() {
    String res = "";
    for(ActionDescriptor descriptor : actionList) {
      res += actionList + "\n";
    }
    return res;
  }

  public ActionDescriptor getNextActionToPerform() {
    //TODO implement fast solution
    return null;
  }

  public static void main(String args[]) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    JsonElement elt = parser.parse(new JsonReader(new FileReader("actionHandler/configCollection.json")));

    ActionCollection collection = new ActionCollection(elt.getAsJsonArray());
    System.out.println(collection);
  }
}
