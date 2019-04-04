package inf112.skeleton.app.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.GUI.Message_ok;
import io.netty.channel.Channel;


public abstract class GameState {
    protected OrthographicCamera camera;
    public GameStateManager gsm;
    protected Channel channel;
    public Stage stage;

    protected GameState(GameStateManager gsm, Channel channel) {
        this.gsm = gsm;
        this.channel = channel;
        camera = new OrthographicCamera();
        stage = new Stage();
    }

    /**
     * Add message-box to screen which user may remove by clicking "OK".
     * @param message to write in box
     */
    public void addMessageToScreen(String message) {
        // Create new message-box
        Message_ok messageBox = new Message_ok(message);
        messageBox.setSize(Math.max(300, messageBox.getMinWidth()), messageBox.getMinHeight());
        messageBox.setPosition(Gdx.graphics.getWidth() / 2f - messageBox.getWidth() / 2f,Gdx.graphics.getHeight() / 2f - messageBox.getHeight() / 2f);

        messageBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // If ok-button clicked, remove message-box from screen
                if (messageBox.isOK) {
                    messageBox.remove();
                }
            }
        });

        // Add message-box to screen
        stage.addActor(messageBox);
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
    public abstract void resize(int width, int height);
}