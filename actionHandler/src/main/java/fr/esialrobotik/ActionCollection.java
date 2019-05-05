package fr.esialrobotik;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class ActionCollection {
  private List<ActionDescriptor> actionList;
  private int currentIndex;
  private JsonElement jsonElement;

  @Inject
  public ActionCollection(@Named("commandFile") String filepath) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    jsonElement = parser.parse(new JsonReader(new FileReader(filepath)));

    currentIndex = 0;
  }

  public void prepareActionList(boolean isColor0) {
    actionList = new ArrayList<>();

    for(JsonElement element : jsonElement.getAsJsonObject().getAsJsonArray(isColor0 ? "couleur0" : "couleur3000")) {
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
    if (currentIndex >= actionList.size()) {
      return null;
    }
    return actionList.get(currentIndex++);
  }

  public List<ActionDescriptor> getActionList() {
    return actionList;
  }

  public void setActionList(List<ActionDescriptor> actionList) {
    this.actionList = actionList;
  }

  public static void main(String args[]) throws FileNotFoundException {
    ActionCollection collection = new ActionCollection("actionHandler/configCollection.json");
    System.out.println(collection);
  }
}
