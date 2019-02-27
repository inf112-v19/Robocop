package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.menu.ButtonGenerator;
import io.netty.channel.Channel;

public class HUD {
    private BitmapFont font;
    private SpriteBatch hudBatch; // Sprite batch which won't follow the camera...
    private ButtonGenerator btngen;
    private TextButton to_mainMenu;
    private GameStateManager gsm;
    private InputContainer ic;
    Channel channel;

    public HUD(GameStateManager gameStateManager, InputContainer inputContainer, final Channel channel) {
        gsm = gameStateManager;
        ic = inputContainer;

        hudBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.RED);

        btngen = new ButtonGenerator();
        to_mainMenu = btngen.generate("Main menu");
        to_mainMenu.setTransform(true);
        to_mainMenu.setScale(0.5f);
        to_mainMenu.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gsm.set(new State_MainMenu(gsm, channel));
                    }
                }
        );

        ic.addActor(to_mainMenu);
    }

    public void dispose() {
        font.dispose();
    }

    public void render(SpriteBatch sb) {
        hudBatch.begin();
        font.draw(hudBatch , "fps: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight()-10);
        to_mainMenu.setPosition(1f, Gdx.graphics.getHeight() - to_mainMenu.getHeight() * 0.5f - 1);
        to_mainMenu.draw(hudBatch, 1f);
        hudBatch.end();
    }

    public void resize(int width, int height) {

    }
}
