package inf112.skeleton.app.board.entity;

public enum EntityType {


    PLAYER("player", 64, 64);

    private String id;
    private int width, height;


    EntityType(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
