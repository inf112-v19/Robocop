package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class GameBoard {

    public abstract void render(OrthographicCamera camera);

    public abstract void update(float delta);

    public abstract void dispose();

    /**
     * Gets a tile by pixel position within the board, at a specified layer.
     * 指定したレイヤーのボード上のピクセル位置を使ってタイルを見つけます。
     *
     * @param layer
     * @param x
     * @param y
     * @return
     */
    public TileDefinition getTileDefinitionByLocation(int layer, float x, float y) {
        return this.getTileDefinitionByCoordinate(
                layer,
                (int) (x / TileDefinition.TILE_SIZE),
                (int) (y / TileDefinition.TILE_SIZE)
        );
    }

    /**
     * Gets a tile at a specified coordinate on the game board.
     * ボード上の座標を使ってタイルを見つけます。
     *
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row);
    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLayers();


}
