package fr.esialrobotik;

import com.google.gson.JsonObject;
import esialrobotik.ia.asserv.Position;
import fr.esialrobotik.data.table.Point;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class Step {
  public enum Type{
    DEPLACEMENT,
    MANIPULATION
  }

  public enum SubType {
    NONE,
    GO,
    FACE,
    GOTO
  }

  private int stepId;

  //THis id is to specify which action to call
  private int idAction;
  // Start position of the action
  private Position position;
  private Type actionType;
  private SubType subType;
  private int distance;

  public Step(JsonObject configNode) {
    this.distance = -1;


    this.stepId = configNode.get("id").getAsInt();
    this.idAction = configNode.get("actionId").getAsInt();
    String type = configNode.get("type").getAsString();
    if(type.equals("deplacement")) {
      actionType = Type.DEPLACEMENT;
    }
    else if(type.equals("manipulation")) {
      actionType = Type.MANIPULATION;
    }

    if(configNode.has("subtype")) {
      String temp = configNode.get("subtype").getAsString();
      if(temp.equals("go")) {
        this.subType = SubType.GO;
        this.distance = configNode.get("dist").getAsInt();
      }
      else if(temp.equals("goto")) {
        this.subType = SubType.GOTO;
        this.position = new Position(configNode.get("positionX").getAsInt(),
                configNode.get("positionY").getAsInt());
      }
      else if(temp.equals("face")) {
        this.subType = SubType.FACE;
        this.position = new Position(configNode.get("positionX").getAsInt(),
                configNode.get("positionY").getAsInt());
      }
    }
    else {
      subType = SubType.NONE;
    }
  }

  public Type getActionType() {
    return this.actionType;
  }

  public Position getEndPosition() {
    return this.position;
  }

  public int getActionId() {
    return this.idAction;
  }

  public int getDistance() {
    return this.distance;
  }

  public SubType getSubType() {
    return subType;
  }

  public String toString() {
    String res  = "id : " + stepId;
    res += "\nposition : " + position;
    res += "\nactionId: " + idAction;
    res += "\ntype: " + actionType;
    res += "\nsubtype: " + subType;
    res += "\ndistance: " + distance;
    return res;
  }
}
