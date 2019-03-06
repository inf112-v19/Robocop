package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;

class SingleStatus{
    TextButton lives, damage;

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
    public GameStateManager gsm;
    public InputMultiplexer im;
    public Channel channel;

    Stage stage;

    BitmapFont font;
    TextButton.TextButtonStyle style_Lives, style_Damage;
    TextField.TextFieldStyle txtStyle;

    HashMap<String, SingleStatus> statusUpdater;
    Table statusbar;

    public static int height = 30;

    public Status (GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;
        this.im = inputMultiplexer;
        this.channel = channel;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(1.7f);

        Drawable d;

        d =  new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("status_Life.png"))));
        style_Lives = new TextButton.TextButtonStyle(d,d,d,font);
        style_Lives.fontColor = Color.BLACK;

        d =  new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("status_DamageNoExclSquished.png"))));
        style_Damage = new TextButton.TextButtonStyle(d,d,d,font);
        style_Damage.fontColor = Color.BLACK;

        txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = new BitmapFont();
        txtStyle.fontColor = Color.BLACK;

        statusUpdater = new HashMap<>();
        statusbar = new Table();

        stage.addActor(statusbar);
    }

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

    public void updateLives(String roboName, int lives) {
        statusUpdater.get(roboName).updateLives(lives);
    }

    public void updateDamage(String roboName, int damage) {
        statusUpdater.get(roboName).updateDamage(damage);
    }

    public void render(Batch sb) {
        stage.draw();
    }

    public void setPosition(int x, int y) {
        statusbar.setPosition(x, y);
    }
}
