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
    String name;

    public UpdatePlayerPacket(String name, Directions direction, int movingTiles, Vector2 fromTile, Vector2 toTile) {
        this.name = name;
        this.direction = direction;
        this.movingTiles = movingTiles;
        this.fromTile = fromTile;
        this.toTile = toTile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public int getMovingTiles() {
        return movingTiles;
    }

    public void setMovingTiles(int movingTiles) {
        this.movingTiles = movingTiles;
    }

    public Vector2 getFromTile() {
        return fromTile;
    }

    public void setFromTile(Vector2 fromTile) {
        this.fromTile = fromTile;
    }

    public Vector2 getToTile() {
        return toTile;
    }

    public void setToTile(Vector2 toTile) {
        this.toTile = toTile;
    }

    public static UpdatePlayerPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
    }
}
