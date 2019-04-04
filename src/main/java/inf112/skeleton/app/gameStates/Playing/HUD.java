package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.GUI.ChatBox;
import inf112.skeleton.app.GUI.PlayerDeck;
import inf112.skeleton.app.GUI.Timer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.data.ChatMessagePacket;
import io.netty.channel.Channel;

import java.util.Collection;

public class HUD {
    private BitmapFont font;
    private GameStateManager gsm;
    private Stage stage;
    private PlayerDeck playerDeck = null;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;
    public ChatBox gameChat;
    public boolean gameChatIsTouched;
    public Timer turnTimer;

    private Status status;

    /**
     * Initializes display which may be seen on top of actual game.
     * @param gameStateManager lets you manage game-states
     * @param inputMultiplexer lets multiple objects receive input
     * @param channel lets you communicate with server
     */
    HUD(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;

        this.inputMultiplexer = inputMultiplexer;
        this.channel = channel;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        inputMultiplexer.addProcessor(stage);

        font = new BitmapFont();
        font.setColor(Color.RED);


        setupGameChatAndPushWelcome();

        // Status-bar
        status = new Status(gsm,inputMultiplexer,channel);

        turnTimer = new Timer("Time to choose cards: ", 30000, 1);
        turnTimer.setSize(turnTimer.getMinWidth(), turnTimer.getMinHeight());
        turnTimer.setPosition(Gdx.graphics.getWidth() / 2f - turnTimer.getMinWidth() / 2f, Gdx.graphics.getHeight() - turnTimer.getHeight() - 5);

        playerDeck = new PlayerDeck(gsm, inputMultiplexer, channel);
        turnTimer.start();

        RoboRally.gameBoard.hud = this;
    }

    /**
     * Get the Player Deck
     * @return PlayerDeck
     */
    public PlayerDeck getPlayerDeck(){
        return playerDeck;
    }

    /**
     * Dispose of data-structures used by HUD
     */
    public void dispose() {
        font.dispose();
    }

    /**
     * Draw HUD to screen
     * @param sb sprite-batch
     */
    public void render(SpriteBatch sb) {
        if (!Gdx.input.isTouched())
            gameChatIsTouched = false;


        sb.setProjectionMatrix(stage.getCamera().combined);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
        if (status != null) {
            if (RoboRally.gameBoard.getPlayers().size() + 1 != status.statusUpdater.size()) {
                status.statusUpdater.clear();
                status.add(RoboRally.gameBoard.myPlayer);
                for (Player player : (Collection<Player>) RoboRally.gameBoard.getPlayers().values()) {
                    status.add(player);
                }
            }
        }
        status.render(sb);
        if (playerDeck != null) {
            playerDeck.render(sb);
        }

        sb.begin();
        font.draw(sb, "fps: " + Gdx.graphics.getFramesPerSecond(), stage.getWidth() - 60, stage.getHeight() - 10);
        if (!turnTimer.isFinished) {
            turnTimer.draw(sb, 1);
        }
        sb.end();
    }

    /**
     * Update stage viewport of HUD whenever screen is resized
     * @param width new width of screen
     * @param height new height of screen
     */
    public void resize(int width, int height) {
        // TODO: Fix bug where event-listener click-box won't move along with button.
        stage.getViewport().update(width,height);
        if(playerDeck != null) {
            playerDeck.resize(width, height);
        }
    }

    /**
     * Setup game chat and give welcome message to player.
     */
    private void setupGameChatAndPushWelcome() {
        gameChat = new ChatBox(channel);
        gameChat.addMessage(new ChatMessagePacket("Welcome to RoboCop. You have 30 seconds to choose cards"));
        gameChat.addMessage(new ChatMessagePacket("[INFO]: Available commands: "));
        gameChat.addMessage(new ChatMessagePacket("[INFO]:     \"!move <direction> <lenght>\" (north,south,east,west)"));
        gameChat.addMessage(new ChatMessagePacket("[INFO]:     \"!players\""));
        gameChat.setSize(600,200);

        gameChat.setTouchable(Touchable.enabled);
        gameChat.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameChatIsTouched = true;
                return true;
            }
        });
        stage.addActor(gameChat);
    }
}
