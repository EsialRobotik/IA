package fr.esialrobotik;

import com.google.gson.JsonObject;
import esialrobotik.ia.asserv.Position;

/**
 * Created by Guillaume on 18/05/2017.
 */
public class Step {
    public enum Type {
        DEPLACEMENT,
        MANIPULATION
    }

    public enum SubType {
        NONE,
        GO,
        FACE,
        GOTO,
        GOTO_BACK,
        GOTO_ASTAR
    }

    private String desc;
    private int stepId;

    //THis id is to specify which action to call
    private int idAction;
    // Start position of the action
    private Position position;
    private Type actionType;
    private SubType subType;
    private int distance;
    private int timeout;

    private boolean yPositiveExclusive = false;
    private boolean yNegativeExclusive = false;

    public Step(JsonObject configNode) {
        this.distance = -1;
        this.timeout = -1;

        this.desc = configNode.get("desc").getAsString();
        this.stepId = configNode.get("id").getAsInt();
        this.idAction = configNode.get("actionId").getAsInt();
        String type = configNode.get("type").getAsString();
        if (type.equals("deplacement")) {
            actionType = Type.DEPLACEMENT;
        } else if (type.equals("manipulation")) {
            actionType = Type.MANIPULATION;
        }

        if (configNode.has("subtype")) {
            String temp = configNode.get("subtype").getAsString();
            if (temp.equals("go")) {
                this.subType = SubType.GO;
                this.distance = configNode.get("dist").getAsInt();
                this.timeout = configNode.get("timeout").getAsInt();
            } else if (temp.equals("goto")) {
                this.subType = SubType.GOTO;
                this.position = new Position(configNode.get("positionX").getAsInt(), configNode.get("positionY").getAsInt());
            } else if (temp.equals("face")) {
                this.subType = SubType.FACE;
                this.position = new Position(configNode.get("positionX").getAsInt(), configNode.get("positionY").getAsInt());
            } else if (temp.equals("goto_back")) {
                this.subType = SubType.GOTO_BACK;
                this.position = new Position(configNode.get("positionX").getAsInt(), configNode.get("positionY").getAsInt());
            } else if (temp.equals("goto_astar")) {
                this.subType = SubType.GOTO_ASTAR;
                this.position = new Position(configNode.get("positionX").getAsInt(), configNode.get("positionY").getAsInt());
            }

            if (configNode.has("yPositiveExclusive")) {
                yPositiveExclusive = configNode.get("yPositiveExclusive").getAsBoolean();
            } else if (configNode.has("yNegativeExclusive")) {
                yNegativeExclusive = configNode.get("yNegativeExclusive").getAsBoolean();
            }
        } else {
            subType = SubType.NONE;
        }
    }

    public String getDesc() {
        return desc;
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

    public boolean isyPositiveExclusive() {
        return yPositiveExclusive;
    }

    public boolean isyNegativeExclusive() {
        return yNegativeExclusive;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "Step{" +
                "desc='" + desc + '\'' +
                ", stepId=" + stepId +
                ", idAction=" + idAction +
                ", position=" + position +
                ", actionType=" + actionType +
                ", subType=" + subType +
                ", distance=" + distance +
                ", yPositiveExclusive=" + yPositiveExclusive +
                ", yNegativeExclusive=" + yNegativeExclusive +
                '}';
    }

}
