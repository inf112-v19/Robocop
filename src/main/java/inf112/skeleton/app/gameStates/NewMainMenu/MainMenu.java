package inf112.skeleton.app.gameStates.NewMainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;

enum MenuStates {
    Welcome,
    Lobbies,
}

public class MainMenu extends GameState {
    private final Color color_primary   = new Color(0.6f,0.4f,0.2f,1),
                        color_secondary = new Color(0.773f, 0.612f, 0.424f, 1);
    private Stage stage;
    protected Stage mainStage;
    private ShapeRenderer shape;
    private Table layout;
    private MenuStates menuState = MenuStates.Welcome;

    private Table h1, h2, main;
    ImageTextButton.ImageTextButtonStyle h2_btn_style_focused, h2_btn_style_unfocused;

    HashMap<String, MenuTab> tabs;
    HashMap<String, ImageTextButton> tabButtons;

    ImageTextButton currentTab;


    private final int   pad_leftRight       = 7,
                        h1_height           = 60,
                        h1_pad_topBottom    = 8,
                        h2_height           = 45,
                        h2_pad_topBottom    = 2,
                        main_height         = 615,
                        main_padding        = 13;

    public MainMenu(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        shape = new ShapeRenderer();

        layout = new Table();
        layout.setSize(stage.getWidth(), stage.getHeight());

        tabs = new HashMap<>();
        tabButtons = new HashMap<>();

        // Add header 1 (Text: "RoboCop"):
        h1 = new Table();
        h1.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/h1.png")))));
        h1.setSize(stage.getWidth(), h1_height);
        layout.add(h1).expandX().height(h1_height);
        layout.row();


        // Add header 2 (Textbuttons: "Welcome", "Lobbies"):
        h2 = new Table();
        h2.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/h2.png")))));
        h2.setSize(stage.getWidth(), h2_height);
        h2.left();
        h2.add().width(0).padLeft(5);

        layout.add(h2).expandX().height(h2_height);
        layout.row();


        // Add button styles for header 2
        Drawable    d_btn_f = new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("graphics/ui/MainMenu/btn_rounded_focused.png")))),
                    d_btn = new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("graphics/ui/MainMenu/btn_rounded_nonfocused.png"))));

        h2_btn_style_focused = new ImageTextButton.ImageTextButtonStyle(d_btn_f, d_btn_f, d_btn_f, new BitmapFont());
        h2_btn_style_focused.fontColor = Color.BLACK;
        h2_btn_style_unfocused = new ImageTextButton.ImageTextButtonStyle(d_btn, d_btn, d_btn, new BitmapFont());
        h2_btn_style_unfocused.fontColor = Color.BLACK;


        // Add bar with main content:
        main = new Table();
        main.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/main.png")))));
        main.setSize(stage.getWidth(), main_height);
        main.left();
        layout.add(main).expand();
        layout.row();

        // Add tabs
        addTab("Welcome", new Tab_Welcome(gsm, channel));
        addTab("Welcome2", new Tab_Welcome(gsm, channel));
        addTab("Lobbies", new Tab_Lobbies(gsm, channel));



        stage.addActor(layout);


        // tmp
        Gdx.input.setInputProcessor(stage);

        Tab_Welcome tab = (Tab_Welcome) tabs.get("Welcome");

        tab.addFriend("SteffenMistro", true);
        tab.addFriend("SteffenMistroAngelus", true);
        tab.addFriend("SteffenMistroFatherOfKings", true);

        tab.setFriendStatus("SteffenMistroAngelus", false);
    }

    public void tabButtonAction (ImageTextButton textButton) {
        if (currentTab == textButton) {
            return;
        }

        // Switch tab animations
        if (currentTab != null) {
            currentTab.setStyle(h2_btn_style_unfocused);
        }
        currentTab = textButton;
        currentTab.setStyle(h2_btn_style_focused);

        // Update display
        main.clearChildren();
        main.add(tabs.get(currentTab.getText().toString()).display).pad(main_padding);
        main.row();
    }


    public void addTab(String tabname, MenuTab tab) {
        ImageTextButton newTabButton = new ImageTextButton(tabname, h2_btn_style_unfocused);
        h2.add(newTabButton).height(h2_height-2).pad(1).width(100).padLeft(10);

        tabButtons.put(tabname, newTabButton);
        tabs.put(tabname, tab);

        newTabButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                tabButtonAction((ImageTextButton) actor);
            }
        });

        if (currentTab == null)
            tabButtonAction(newTabButton);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(color_primary.r,color_primary.g, color_primary.b, color_primary.a);
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void focus() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
