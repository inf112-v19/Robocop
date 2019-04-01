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
    LASERSOURCE(18, true, Directions.NORTH, "Laser-Source"),
    LASER(19, true, Directions.NORTH, "Laser"),
    LASERCROSS(20, true, Directions.NORTH, "LaserCross"),
    BLACK_HOLE(21, true, Directions.NORTH, "BlackHole");


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

    /**
     * Get tile id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Check if tile is collidable
     * @return true if collidable
     */
    public boolean isCollidable() {
        return collidable;
    }

    /**
     * get the default direction of a tile
     * will be used for conveyor belts and gyros to check rotation
     * @return Direction the default facing direction
     */
    public Directions getDefaultFace() {
        return defaultFace;
    }

    /**
     * Get the tile name
     * @return tile name
     */
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

    /**
     * Get a TileDefinition by id
     * @param id
     * @return TileDefinition
     */
    public static TileDefinition getTileById(int id) {
        return tileMap.get(id);
    }
}
