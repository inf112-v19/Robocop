package inf112.skeleton.common.specs;

public enum MapFile {
    CROSS("board/Cross.tmx", "Cross");
    private String filename;
    private String name;

    MapFile(String filename, String name) {
        this.filename = filename;
        this.name = name;
    }

}
