package inf112.skeleton.common.packet.data;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;

public class UpdatePlayerPacket implements PacketData {
    Direction direction;
    int movingTiles;
    Vector2 fromTile;
    Vector2 toTile;
    String uuid;
    int currentHP;

    public UpdatePlayerPacket(String uuid, Direction direction, int movingTiles, Vector2 fromTile, Vector2 toTile, int currentHP) {
        this.uuid = uuid;
        this.direction = direction;
        this.movingTiles = movingTiles;
        this.fromTile = fromTile;
        this.toTile = toTile;
        this.currentHP = currentHP;

    }

    public String getUUID() {
        return uuid;
    }


    public Direction getDirection() {
        return direction;
    }


    public int getMovingTiles() {
        return movingTiles;
    }


    public Vector2 getFromTile() {
        return fromTile;
    }


    public Vector2 getToTile() {
        return toTile;
    }

    public int getCurrentHP() { return currentHP; }


    public static UpdatePlayerPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
    }
}
