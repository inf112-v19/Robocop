package inf112.skeleton.server.WorldMap.entity;

import inf112.skeleton.common.specs.Direction;

public class ForceMovement {
    Direction direction;
    int amount;
    Player moving;
    Player action;
    boolean pushed;
    boolean tileAction;

    public ForceMovement(Direction direction, int amount, Player moving, Player action, boolean pushed) {
        this.direction = direction;
        this.amount = amount;
        this.moving = moving;
        this.action = action;
        this.pushed = pushed;
    }

    public ForceMovement(Direction direction, int amount, Player moving, boolean pushed, boolean tileAction) {
        this.direction = direction;
        this.amount = amount;
        this.moving = moving;
        this.pushed = pushed;
        this.tileAction = tileAction;
    }

    public boolean isTileAction() {
        return tileAction;
}

    public Direction getDirection() {
        return direction;
    }

    public int getAmount() {
        return amount;
    }

    public Player getMoving() {
        return moving;
    }

    public Player getAction() {
        return action;
    }

    public boolean isPushed() {
        return pushed;
    }
}
