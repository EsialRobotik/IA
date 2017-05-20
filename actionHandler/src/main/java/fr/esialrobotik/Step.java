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
    MANIPULATION,
    DEPLACEMENT_ALWAYS, //Not the best name. If someone as better enjoy
    ROTATION
  }

  private int stepId;

  //THis id is to specify which action to call
  private int idAction;
  // Start position of the action
  private Position position;
  private Type actionType;

  public Step(JsonObject configNode) {
    this.stepId = configNode.get("id").getAsInt();
    this.idAction = configNode.get("actionId").getAsInt();
    this.position = new Position(configNode.get("positionX").getAsInt(),
            configNode.get("positionY").getAsInt(),
            configNode.get("theta").getAsDouble());
    String type = configNode.get("type").getAsString();
    if(type.equals("deplacement")) {
      actionType = Type.DEPLACEMENT;
    }
    else if(type.equals("deplacement_always")) {
      actionType = Type.DEPLACEMENT_ALWAYS;
    }
    else if(type.equals("rotation")) {
      actionType = Type.ROTATION;
    }
    else if(type.equals("manipulation")) {
      actionType = Type.MANIPULATION;
    }
  }

  public Type getActionType() {
    return this.actionType;
  }

  public Position getEndPosition() {
    return this.position;
  }


  public String toString() {
    String res  = "id : " + stepId;
    res += "\nposition : " + position;
    res += "\nactionId: " + idAction;
    res += "\ntype: " + actionType;
    return res;
  }
}
