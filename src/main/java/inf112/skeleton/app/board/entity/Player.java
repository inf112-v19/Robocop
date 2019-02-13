package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.board.GameBoard;

import static inf112.skeleton.app.RoboRally.inputHandler;

public class Player extends Entity {


    Texture image;


    public Player(float x, float y, GameBoard board) {
        super(x, y, EntityType.PLAYER, board);
        image = new Texture("robot.png");
    }

    @Override
    public void update() {
        if(inputHandler.keyUp(Input.Keys.W)) {
            this.pos.add(0,1);
        } else if(inputHandler.keyUp(Input.Keys.S)) {
            this.pos.add(0,-1);
        }
        if(inputHandler.keyUp(Input.Keys.A)) {
            this.pos.add(-1,0);
        } else if(inputHandler.keyUp(Input.Keys.D)) {
            this.pos.add(1,0);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x*64, pos.y*64, getWidth(), getHeight());
    }
}
