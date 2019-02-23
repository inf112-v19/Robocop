package inf112.skeleton.app.board;

import java.util.HashMap;

public enum TileDefinition {
    BACKGROUND(1, true, "Background"),
    BLANK(2, false, "Blank"),
    BIDIRECTION(3, false, "BiDirectional"),
    HORISONTAL(4, false, "Horisontal"),
    VERTICAL(5, false, "Vertical"),
    TBRACKET(6, false, "T-Bracket"),
    LBRACKET(7, false, "L-Bracket");


    public static final int TILE_SIZE = 64;
    private int id;
    private boolean collidable;
    private String name;

    TileDefinition(int id, boolean collidable, String name) {
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
        tileMap = new HashMap<>();
        for (TileDefinition tileDefinition : TileDefinition.values()) {
            tileMap.put(tileDefinition.getId(), tileDefinition);

        }
    }

    public static TileDefinition getTileById(int id) {
        return tileMap.get(id);
    }
}
