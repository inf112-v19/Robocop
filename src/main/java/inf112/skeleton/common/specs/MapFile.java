package inf112.skeleton.common.specs;

import java.util.HashMap;

public enum MapFile {
    CROSS("board/Cross.tmx", "Cross", MapDifficulty.Easy, "A cross...");
    public String filename;
    public String name;
    public MapDifficulty mapDifficulty;
    public String description;

    MapFile(String filename, String name, MapDifficulty mapDifficulty, String description) {
        this.filename = filename;
        this.name = name;
        this.mapDifficulty = mapDifficulty;
        this.description = description;
    }

    private static HashMap<String, MapFile> MapList;

    static {
        MapList = new HashMap<>();
        for (MapFile mapFile : MapFile.values()) {
            MapList.put(mapFile.name, mapFile);

        }
    }

}
