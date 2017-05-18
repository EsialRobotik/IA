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

  private int stepId;

  //THis id is to specify which action to call
  private int idAction;
  // Start position of the action
  private Position position;

  public Step(JsonObject configNode) {
    this.stepId = configNode.get("id").getAsInt();
    this.idAction = configNode.get("actionId").getAsInt();
    this.position = new Position(configNode.get("positionX").getAsInt(),
            configNode.get("positionY").getAsInt(),
            configNode.get("theta").getAsDouble());
  }

  public String toString() {
    String res  = "id : " + stepId;
    res += "\nposition : " + position;
    res += "\nactionId: " + idAction;
    return res;
  }
}
