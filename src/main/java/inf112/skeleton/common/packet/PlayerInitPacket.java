package inf112.skeleton.common.packet;


import com.badlogic.gdx.math.Vector2;

public class PlayerInitPacket implements PacketData {
    Vector2 pos;
    Vector2 movingTo;
    int health;

    public PlayerInitPacket(Vector2 pos, int health) {
        this.pos = pos;
        this.movingTo = new Vector2(pos.x, pos.y);
        this.health = health;
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
}
