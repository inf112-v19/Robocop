package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Direction;

public class PlayerBackup {
    Vector2 currentPos;
    Direction direction;
    int currentHP;

    public PlayerBackup(Player toBackup) {
        this.currentPos = new Vector2(toBackup.getCurrentPos().x, toBackup.getCurrentPos().y);
        this.direction = toBackup.getDirection();
        this.currentHP = toBackup.getCurrentHP();
    }
}
