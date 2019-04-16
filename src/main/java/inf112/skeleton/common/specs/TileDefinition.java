package inf112.skeleton.common.specs;

import java.util.HashMap;

public enum TileDefinition {
    BACKGROUND(1, false, Direction.NORTH, "Background"),
    GROUND(2, true, Direction.NORTH, "Ground"),
    BIDIRECTION(3, true, Direction.NORTH, "BiDirectional (Unused)"),
    BELT_HORISONTAL(4, true, Direction.EAST, "Horisontal Belt"),
    VERTICAL(5, true, Direction.NORTH, "Vertical"),
    TBRACKET(6, true, Direction.NORTH, "T-Bracket"),
    TBRACKETFLIPPED(7, true, Direction.NORTH, "T-BracketFlipped"),
    RIGHTTURN(8, true, Direction.EAST, "RightTurn"),
    LEFTTURN(9, true, Direction.WEST, "LeftTurn"),
    LASERSOURCE(18, true, Direction.NORTH, "Laser-Source"),
    LASER(19, true, Direction.NORTH, "Laser"),
    LASERCROSS(20, true, Direction.NORTH, "LaserCross"),
    BLACK_HOLE(21, true, Direction.NORTH, "BlackHole"),
    WALL(22, true, Direction.WEST, "Wall"),
    LWALL(23, true, Direction.WEST, "L-Wall"),
    DLASERSOURCE(24, true, Direction.NORTH, "Double-Laser-Source"),
    DLASER(25, true, Direction.NORTH, "Double-Laser"),
    DLASERCROSS(26, true, Direction.NORTH, "Double-LaserCross"),
    DLASERSINGLECROSS(27, true, Direction.NORTH, "Double-Laser-Single-Cross"),
    WRENCH(28, true, Direction.NORTH, "Wrench"),
    DWRENCH(29, true, Direction.NORTH, "Double-Wrench");



    public static final int TILE_SIZE = 64;
    private int id;
    private boolean collidable;
    private Direction defaultFace;
    private String name;

    TileDefinition(int id, boolean collidable, Direction defaultFace, String name) {
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
    public Direction getDefaultFace() {
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
