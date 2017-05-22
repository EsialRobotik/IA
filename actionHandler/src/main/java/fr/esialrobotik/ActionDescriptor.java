package fr.esialrobotik;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class ActionDescriptor {

  private String desc;
  private int objectiveId;
  private int points;
  private int priority;

  private List<Step> stepList;
  private int stepIndex;

  public ActionDescriptor(JsonObject object) {
    stepList = new ArrayList<Step>();
    stepIndex = -1;

    desc = object.get("desc").getAsString();
    objectiveId = object.get("id").getAsInt();
    points = object.get("points").getAsInt();
    priority = object.get("priorite").getAsInt();

    for(JsonElement elt : object.get("taches").getAsJsonArray()) {
      stepList.add(new Step(elt.getAsJsonObject()));
    }
  }

  public String toString() {
    String res = "id : " + objectiveId;
    res += "\npoints: " + points;
    res += "\ntemps : " + priority;
    for(Step s : stepList) {
      res += "\n" + s.toString();
    }
    return res;
  }

  public boolean isActionStarted() {
    return stepIndex < 0;
  }

  public boolean hasNextStep() {
    return this.stepIndex < stepList.size() - 1 ;
  }

  public Step getNextStep() {
    ++stepIndex;
    return this.stepList.get(stepIndex);
  }

  public Step getCurrentStep() {
    return stepList.get(stepIndex);
  }

  public String getDesc() {
    return desc;
  }

  public static void main(String[] args) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    JsonElement elt = parser.parse(new JsonReader(new FileReader("actionHandler/configAction.json")));

    ActionDescriptor actionDescriptor = new ActionDescriptor(elt.getAsJsonObject());
    System.out.println(actionDescriptor);

    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println("######" + actionDescriptor.getCurrentStep().getEndPosition());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
    System.out.println(actionDescriptor.getNextStep().toString());
    System.out.println(actionDescriptor.hasNextStep());
  }
}
