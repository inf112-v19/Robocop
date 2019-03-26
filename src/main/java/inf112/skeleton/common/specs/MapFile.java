package inf112.skeleton.common.specs;

public enum MapFile {
    CROSS("board/Cross.tmx", "Cross");
    public String filename;
    public String name;

    MapFile(String filename, String name) {
        this.filename = filename;
        this.name = name;
    }

}
