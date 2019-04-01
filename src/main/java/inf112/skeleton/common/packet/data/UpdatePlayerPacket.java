package inf112.skeleton.common.packet.data;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;

public class UpdatePlayerPacket implements PacketData {
    Directions direction;
    int movingTiles;
    Vector2 fromTile;
    Vector2 toTile;
    String uuid;

    public UpdatePlayerPacket(String uuid, Directions direction, int movingTiles, Vector2 fromTile, Vector2 toTile) {
        this.uuid = uuid;
        this.direction = direction;
        this.movingTiles = movingTiles;
        this.fromTile = fromTile;
        this.toTile = toTile;
    }

    public String getUUID() {
        return uuid;
    }


    public Directions getDirection() {
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


    public static UpdatePlayerPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
    }
}
