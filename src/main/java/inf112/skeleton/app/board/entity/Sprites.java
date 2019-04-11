package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import inf112.skeleton.common.specs.Direction;

public class Sprites {
    public static TextureAtlas[] textureAtlases;
    public static Animation[][] robotAnimations;
    public static Animation[] animation_flag;
    public static void setup(){
        textureAtlases = new TextureAtlas[9];
        robotAnimations = new Animation[8][4];
        animation_flag = new Animation[2];

        textureAtlases[0] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBlue2.atlas"));
        textureAtlases[1] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesDarkGreen.atlas"));
        textureAtlases[2] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesGreen.atlas"));
        textureAtlases[3] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesOrange.atlas"));
        textureAtlases[4] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesPink.atlas"));
        textureAtlases[5] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesRed.atlas"));
        textureAtlases[6] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSides.atlas"));
        textureAtlases[7] = new TextureAtlas(Gdx.files.internal("graphics/sprites/robots/robotAllSidesBrown.atlas"));
        for (int i = 0; i < 8; i++) {
            robotAnimations[i][Direction.NORTH.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_North"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.SOUTH.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_South"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.EAST.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_East"), Animation.PlayMode.LOOP);
            robotAnimations[i][Direction.WEST.ordinal()] = new Animation(0.1f, textureAtlases[i].findRegions("robotAllSides_West"), Animation.PlayMode.LOOP);
        }
        textureAtlases[8] = new TextureAtlas(Gdx.files.internal("graphics/sprites/flag.atlas"));
        animation_flag[0] = new Animation(0.1f,textureAtlases[8].findRegions("flag_Active"), Animation.PlayMode.LOOP);
        animation_flag[1] = new Animation(0.1f,textureAtlases[8].findRegions("flag_Inactive"), Animation.PlayMode.LOOP);
    }

}
