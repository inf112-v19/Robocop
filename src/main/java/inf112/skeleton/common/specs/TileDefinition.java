package inf112.skeleton.common.specs;

import java.util.HashMap;

public enum TileDefinition {
    BACKGROUND(1, false, Directions.NORTH, "Background"),
    GROUND(2, true, Directions.NORTH, "Ground"),
    BIDIRECTION(3, true, Directions.NORTH, "BiDirectional"),
    HORISONTAL(4, true, Directions.EAST, "Horisontal"),
    VERTICAL(5, true, Directions.NORTH, "Vertical"),
    TBRACKET(6, true, Directions.NORTH, "T-Bracket"),
    TBRACKETFLIPPED(7, true, Directions.NORTH, "T-BracketFlipped"),
    RIGHTTURN(8, true, Directions.EAST, "RightTurn"),
    LEFTTURN(9, true, Directions.WEST, "LeftTurn"),
    LASERSOURCE(11, true, Directions.NORTH, "Laser-Source"),
    LASER(12, true, Directions.NORTH, "Laser"),
    LASERCROSS(13, true, Directions.NORTH, "LaserCross"),
    BLACK_HOLE(14, true, Directions.NORTH, "BlackHole");


    public static final int TILE_SIZE = 64;
    private int id;
    private boolean collidable;
    private Directions defaultFace;
    private String name;

    TileDefinition(int id, boolean collidable, Directions defaultFace, String name) {
        this.id = id;
        this.collidable = collidable;
        this.defaultFace = defaultFace;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public Directions getDefaultFace() {
        return defaultFace;
    }

    public String getName() {
        return name;
    }

    private static HashMap<Integer, TileDefinition> tileMap;

    static {
        tileMap = new HashMap<>();
        for (TileDefinition tileDefinition : TileDefinition.values()) {
            tileMap.put(tileDefinition.getId(), tileDefinition);

        }
    }

    public static TileDefinition getTileById(int id) {
        return tileMap.get(id);
    }
}
