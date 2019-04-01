package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import inf112.skeleton.common.specs.Directions;

public class Sprites {
    public static TextureAtlas[] robotSprites;
    public static Animation[][] animations;
    public static void setup(){
        robotSprites = new TextureAtlas[8];
        animations = new Animation[8][4];

        robotSprites[0] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBlue2.atlas"));
        robotSprites[1] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesDarkGreen.atlas"));
        robotSprites[2] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesGreen.atlas"));
        robotSprites[3] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesOrange.atlas"));
        robotSprites[4] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesPink.atlas"));
        robotSprites[5] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesRed.atlas"));
        robotSprites[6] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSides.atlas"));
        robotSprites[7] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBrown.atlas"));
        for (int i = 0; i < robotSprites.length; i++) {
            animations[i][Directions.NORTH.ordinal()] = new Animation(0.1f, robotSprites[i].findRegions("robotAllSides_North"), Animation.PlayMode.LOOP);
            animations[i][Directions.SOUTH.ordinal()] = new Animation(0.1f, robotSprites[i].findRegions("robotAllSides_South"), Animation.PlayMode.LOOP);
            animations[i][Directions.EAST.ordinal()] = new Animation(0.1f, robotSprites[i].findRegions("robotAllSides_East"), Animation.PlayMode.LOOP);
            animations[i][Directions.WEST.ordinal()] = new Animation(0.1f, robotSprites[i].findRegions("robotAllSides_West"), Animation.PlayMode.LOOP);
        }
    }

}
