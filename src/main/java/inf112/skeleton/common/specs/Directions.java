package inf112.skeleton.common.specs;

import com.badlogic.gdx.math.Vector2;

public enum Directions {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Directions fromString(String str) {
        for (Directions val : Directions.values()) {
            if (val.name().equalsIgnoreCase(str))
                return val;
        }
        return null;
    }

    public static Vector2 vectorDirection(Directions direction) {
        switch(direction) {
            case NORTH:
                return new Vector2(0,1);

            case SOUTH:
                return new Vector2(0,-1);

            case EAST:
                return new Vector2(1,0);

            case WEST:
                return new Vector2(-1,0);

            default:
                return null;
        }
    }
}
