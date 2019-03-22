package inf112.skeleton.common.specs;

import java.util.HashMap;

public enum MapFile {
    CROSS("board/Cross.tmx", "Cross");
    private String filename;
    private String name;

    MapFile(String filename, String name) {
        this.filename = filename;
        this.name = name;
    }

    private static HashMap<String, MapFile> MapList;

    static {
        MapList = new HashMap<>();
        for (MapFile mapFile : MapFile.values()) {
            MapList.put(mapFile.name, mapFile);

        }
    }

}
