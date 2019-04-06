package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.GUI.ChatBox;
import inf112.skeleton.app.GUI.PlayerDeck;
import inf112.skeleton.app.GUI.StatusBar;
import inf112.skeleton.app.GUI.Timer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.data.ChatMessagePacket;
import io.netty.channel.Channel;

import java.util.Collection;

public class HUD {
    private GameStateManager gsm;
    private Stage stage;
    private PlayerDeck playerDeck;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;
    public ChatBox gameChat;
    public boolean gameChatIsTouched;
    public Timer turnTimer;
    private StatusBar statusBar;

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
        RoboRally.gameBoard.hud = this;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        inputMultiplexer.addProcessor(stage);

        setupGameChatAndPushWelcome();

        turnTimer = new Timer("Time to choose cards: ", 30000, 1);
        turnTimer.setSize(turnTimer.getMinWidth(), turnTimer.getMinHeight());
        turnTimer.start();

        playerDeck = new PlayerDeck(gsm, inputMultiplexer, channel);
        statusBar = new StatusBar();

        stage.addActor(turnTimer);
        stage.addActor(statusBar);
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
    }

    /**
     * Draw HUD to screen
     * @param sb sprite-batch
     */
    public void render(SpriteBatch sb) {
        // Part of making sure that the map shouldn't be moved when scrolling in chat
        if (!Gdx.input.isTouched())
            gameChatIsTouched = false;

        // Act stage for certain features as chat-box to work
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));

        // Update players displayed in status-bar.
        if (statusBar != null && RoboRally.gameBoard.getPlayers().size() + 1 != statusBar.size()) {
            statusBar_clearPlayers();
            statusBar_addPlayer(RoboRally.gameBoard.myPlayer.name);
            for (Player player : (Collection<Player>) RoboRally.gameBoard.getPlayers().values()) {
                statusBar_addPlayer(player.name);
            }
        }

        // Set projection matrix for correct positioning on screen.
        sb.setProjectionMatrix(stage.getCamera().combined);

        // Draw stage to screen
        stage.draw();

        // Todo: Make it addable to stage...
        // Render player-deck.
        if (playerDeck != null) {
            playerDeck.render(sb);
        }

        sb.begin();
        RoboRally.graphics.default_markup_font.draw(sb, "[RED]fps: " + Gdx.graphics.getFramesPerSecond(), 15, stage.getHeight() - 10);
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

    public void statusBar_addPlayer(String username) {
        statusBar.addStatus(username);
        statusBar.setPosition(stage.getWidth() - statusBar.getWidth(),stage.getHeight() - statusBar.getHeight());
        turnTimer.setPosition(stage.getWidth() - turnTimer.getMinWidth() - 2,stage.getHeight() - statusBar.getHeight() - turnTimer.getHeight() - 2);
    }
    private void statusBar_clearPlayers() {
        statusBar.clear();
    }

    public void statusBar_addDamage(String username) {
        statusBar.addDamage(username);
    }
    public void statusBar_removeDamage(String username) {
        statusBar.removeDamage(username);
    }
    public void statusBar_addLife(String username) {
        statusBar.addLife(username);
    }
    public void statusBar_removeLife(String username) {
        statusBar.removeLife(username);
    }
    public void statusBar_powerDown(String username, boolean powerDown) {
        statusBar.powerDown(username, powerDown);
    }
}
