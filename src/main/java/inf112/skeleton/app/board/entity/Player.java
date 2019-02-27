package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;

import static inf112.skeleton.app.board.entity.Directions.*;
import static inf112.skeleton.app.board.entity.Directions.values;

public class Player {
    String name;
    Robot robot = null;
    Vector2 initialPos;
    int initalHp;
    Directions initalDirection;
    public Player(String name, Vector2 pos, int hp, Directions directions) {
        this.name = name;
        this.initalHp = hp;
        this.initialPos = pos;
        this.initalDirection = directions;
    }

    public void update() {
        if(robot == null){
            this.robot = new Robot(initialPos.x, initialPos.y);
            RoboRally.gameBoard.addEntity(robot);
        }
    }

}
