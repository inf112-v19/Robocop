package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;

public class Flag {
    private Vector2 pos;
    private int number;

    public Flag(int x, int y, int number) {
        this.pos = new Vector2(x,y);
        this.number = number;
    }

    public Flag(Vector2 pos, int number) {
        this.pos = pos;
        this.number = number;
    }

    public Vector2 getPos() {
        return this.pos;
    }

    public int getNumber() {
        return this.number;
    }
}
