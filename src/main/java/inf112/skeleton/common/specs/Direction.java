package inf112.skeleton.common.specs;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Direction fromString(String str) {
        for (Direction val : Direction.values()) {
            if (val.name().equalsIgnoreCase(str))
                return val;
        }
        return null;
    }
}
