package inf112.skeleton.app.board;

import java.util.HashMap;

public enum TileDefinition {
    BACKGROUND(0, true, "Background"),
    BLANK(1, true, "Blank"),
    BIDIRECTION(2, true, "BiDirectional"),
    HORISONTAL(3, true, "Horisontal"),
    VERTICAL(4, true, "Vertical");



    public static final int TILE_SIZE = 64;
    private int id;
    private boolean collidable;
    private String name;

    private TileDefinition(int id, boolean collidable, String name) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public String getName() {
        return name;
    }

    private static HashMap<Integer, TileDefinition> tileMap;

    static {
        for (TileDefinition tileDefinition : TileDefinition.values()) {
            tileMap.put(tileDefinition.getId(), tileDefinition);

        }
    }

    public static TileDefinition getTileById(int id) {
        return tileMap.get(id);
    }
}
