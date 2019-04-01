package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;

class SingleStatus{
    private TextButton lives, damage;

    SingleStatus (TextButton lives, TextButton damage) {
        this.lives = lives;
        this.damage = damage;
    }

    public void updateLives(int l) {
        lives.setText(""+l);
    }

    public void updateDamage(int d) {
        damage.setText(""+d);
    }
}


public class Status {
    private GameStateManager gsm;
    private InputMultiplexer im;
    private Channel channel;

    private Stage stage;

    private TextButton.TextButtonStyle style_Lives, style_Damage;
    private TextField.TextFieldStyle txtStyle;

    private HashMap<String, SingleStatus> statusUpdater;
    private Table statusbar;

    public static int height = 30;

    /**
     * Initialize status bar.
     * @param gameStateManager helps to get current game-state (State_Playing)
     * @param inputMultiplexer allows multiple objects to receive input
     * @param channel allows communication with server
     */
    public Status (GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;
        this.im = inputMultiplexer;
        this.channel = channel;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));

        style_Lives = RoboRally.graphics.styleFromDrawable(
                RoboRally.graphics.getDrawable(RoboRally.graphics.folder_ui + "status_Life.png"),
                RoboRally.graphics.default_font_1p7,
                Color.BLACK);

        style_Damage = RoboRally.graphics.styleFromDrawable(
                RoboRally.graphics.getDrawable(RoboRally.graphics.folder_ui + "status_DamageNoExclSquished.png"),
                RoboRally.graphics.default_font_1p7,
                Color.BLACK);

        txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = RoboRally.graphics.default_font;
        txtStyle.fontColor = Color.BLACK;

        statusUpdater = new HashMap<>();
        statusbar = new Table();

        stage.addActor(statusbar);
    }

    /**
     * Add a new status-line for a robot.
     * @param roboName name of player
     */
    public void add(String roboName) {
        TextButton btn_lives, btn_damage;
        TextField txt_roboName;

        btn_lives = new TextButton("3", style_Lives);
        btn_damage = new TextButton("0", style_Damage);
        txt_roboName = new TextField(roboName, txtStyle);

        statusbar.add(txt_roboName).size(100,height).right();
        statusbar.add(btn_lives).size(height,height);
        statusbar.add(btn_damage).size(height,height);
        statusbar.row();

        statusUpdater.put(roboName, new SingleStatus(btn_lives, btn_damage));

        statusbar.setSize(100 + 2 * height,height * statusUpdater.size());
        statusbar.setPosition(
                Gdx.graphics.getWidth()-statusbar.getWidth(),
                Gdx.graphics.getHeight()-statusbar.getHeight()-25);
    }

    /**
     * Finds the robot to update and update life-display
     * @param roboName name of player
     * @param lives number of updated lives
     */
    public void updateLives(String roboName, int lives) {
        statusUpdater.get(roboName).updateLives(lives);
    }

    /**
     * Finds the robot to update and update damage-display
     * @param roboName name of player
     * @param damage number of updated lives
     */
    public void updateDamage(String roboName, int damage) {
        statusUpdater.get(roboName).updateDamage(damage);
    }

    /**
     * Draw status-bar to screen.
     * @param sb sprite-batch, not used.
     */
    public void render(Batch sb) {
        stage.draw();
    }

    /**
     * Sets the position of the status bar on screen
     * @param x coordinate
     * @param y coordinate
     */
    public void setPosition(int x, int y) {
        statusbar.setPosition(x, y);
    }
}
