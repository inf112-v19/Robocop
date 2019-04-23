package inf112.skeleton.app.board.entity;

public enum EntityType {

    FLAG("flag", 64, 64),
    ROBOT("robot", 64, 64);

    private String id;
    private int width, height;


    EntityType(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the id of the EntityType
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the width of the EntityType
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the EntityType
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }
}
