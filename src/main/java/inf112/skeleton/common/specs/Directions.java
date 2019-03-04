package inf112.skeleton.common.specs;

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
}
