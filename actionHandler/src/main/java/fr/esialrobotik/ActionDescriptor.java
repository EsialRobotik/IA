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
    stepList = new ArrayList<>();
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
    ActionCollection actionCollection = new ActionCollection("actionHandler/configCollection.json");
    ActionDescriptor actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    boolean positive = false;
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }

    actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }

    actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }

    actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }

    actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }

    actionDescriptor = actionCollection.getNextActionToPerform();
    System.out.println(actionDescriptor);
    while (actionDescriptor.hasNextStep()) {
      Step step = actionDescriptor.getNextStep();
      if ((step != null && step.isyPositiveExclusive() && !positive)
              || (step != null && step.isyNegativeExclusive() && positive)) {
        System.out.println("################################### step qui saute");
        step = actionDescriptor.getNextStep();
      }
      System.out.println(step);
    }
  }
}
