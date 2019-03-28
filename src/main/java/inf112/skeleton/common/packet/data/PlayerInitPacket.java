package inf112.skeleton.common.packet.data;


import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;

public class PlayerInitPacket implements PacketData {
    Vector2 pos;
    Vector2 movingTo;
    int health;
    int slot;
    String name;
    Directions facing;

    public PlayerInitPacket(String name, Vector2 pos, int health, int slot, Directions facing) {
        this.name = name;
        this.pos = pos;
        this.movingTo = new Vector2(pos.x, pos.y);
        this.health = health;
        this.facing = facing;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getMovingTo() {
        return movingTo;
    }

    public void setMovingTo(Vector2 movingTo) {
        this.movingTo = movingTo;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSlot() {
        return slot;
    }

    public Directions getFacing() {
        return facing;
    }

    public void setFacing(Directions facing) {
        this.facing = facing;
    }

    public static PlayerInitPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), PlayerInitPacket.class);
    }

}
