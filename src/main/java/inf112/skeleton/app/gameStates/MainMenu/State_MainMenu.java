package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.common.packet.data.LobbiesListPacket;
import inf112.skeleton.common.packet.data.LobbyJoinResponsePacket;
import inf112.skeleton.common.packet.data.LobbyUpdatePacket;
import inf112.skeleton.common.specs.LobbyInfo;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

enum MenuStates {
    Welcome,
    Lobbies,
}

public class State_MainMenu extends GameState {
    private final Color color_primary   = new Color(0.6f,0.4f,0.2f,1),
                        color_secondary = new Color(0.773f, 0.612f, 0.424f, 1);
    private Stage stage;
    protected Stage mainStage;
    private ShapeRenderer shape;
    private Table layout;
    private MenuStates menuState = MenuStates.Welcome;

    private Table h1, h2, main;
    ImageTextButton.ImageTextButtonStyle h2_btn_style_focused, h2_btn_style_unfocused, h2_btn_style_frozen;

    LinkedHashMap<String, MenuTab> tabs;
    LinkedHashMap<String, ImageTextButton> tabButtons;

    ImageTextButton currentTab;
    Channel channel;

    public InputMultiplexer im;

    public Queue<LobbyJoinResponsePacket> packets_LobbyJoin = new ConcurrentLinkedQueue<>();
    public Queue<LobbiesListPacket> packets_LobbyList = new ConcurrentLinkedQueue<>();
    public Queue<LobbyUpdatePacket> packets_LobbyUpdates = new ConcurrentLinkedQueue<>();


    private final int   pad_leftRight       = 7,
                        h1_height           = 60,
                        h1_pad_topBottom    = 8,
                        h2_height           = 45,
                        h2_pad_topBottom    = 2,
                        main_height         = 615,
                        main_padding        = 13;
    private boolean     isFrozen;

    public State_MainMenu(GameStateManager gameStateManager, Channel ch) {
        super(gameStateManager, ch);
        channel = ch;
        im = new InputMultiplexer();
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        shape = new ShapeRenderer();

        isFrozen = false;

        layout = new Table();
        layout.setSize(stage.getWidth(), stage.getHeight());

        tabs = new LinkedHashMap<>();
        tabButtons = new LinkedHashMap<>();

        // Add button styles
        Drawable    d_btn_f = new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("graphics/ui/MainMenu/btn_rounded_focused.png")))),
                    d_btn = new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("graphics/ui/MainMenu/btn_rounded_nonfocused.png")))),
                    d_btn_frozen = new TextureRegionDrawable(new TextureRegion(
                            new Texture(Gdx.files.internal("graphics/ui/MainMenu/btn_rounded_frozen.png"))));

        h2_btn_style_focused = new ImageTextButton.ImageTextButtonStyle(d_btn_f, d_btn_f, d_btn_f, new BitmapFont());
        h2_btn_style_focused.fontColor = Color.BLACK;
        h2_btn_style_unfocused = new ImageTextButton.ImageTextButtonStyle(d_btn, d_btn, d_btn, new BitmapFont());
        h2_btn_style_unfocused.fontColor = Color.BLACK;
        h2_btn_style_frozen = new ImageTextButton.ImageTextButtonStyle(d_btn_frozen, d_btn_frozen, d_btn_frozen, new BitmapFont());
        h2_btn_style_frozen.fontColor = Color.BLACK;

        // Add header 1 (Text: "RoboCop"):
        h1 = new Table();
        h1.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/h1.png")))));
        h1.setSize(stage.getWidth(), h1_height);
        h1.left();

        ImageTextButton logoutButton = new ImageTextButton("Logout", h2_btn_style_focused);
        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gsm.set(new State_Login(gsm, channel));
            }
        });
        layout.add(h1).expand().height(h1_height).row();

        // Add header 2 (Textbuttons: "Welcome", "Lobbies", "Logout"):
        Table h2_cover = new Table();
        h2_cover.setSize(stage.getWidth(), h2_height);
        h2_cover.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/h2.png")))));

        h2 = new Table();
        h2.setSize(stage.getWidth() - 113, h2_height);
        h2.left();
        h2.add().width(0).padLeft(5);

        h2_cover.add(h2).size(stage.getWidth() - 113, h2_height);
        h2_cover.add(logoutButton).size(100, h2_height-2).row();
        layout.add(h2_cover).size(stage.getWidth(), h2_height).row();

        // Add bar with main content:
        main = new Table();
        main.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/main.png")))));
        main.setSize(stage.getWidth(), main_height);
        main.left();
        layout.add(main).expand().row();

        // Add tabs
        addTab("Lobbies", new Tab_Lobbies(gsm, channel), false);

        stage.addActor(layout);
        Gdx.input.setInputProcessor(im);
        im.addProcessor(stage);
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


    public void addTab(String tabname, MenuTab tab, boolean goTo) {
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

        if (currentTab == null || goTo)
            tabButtonAction(newTabButton);
    }

    public void removeCurrentTab() {
        String removeName = currentTab.getText().toString();
        tabButtons.remove(removeName);
        tabs.remove(removeName);

        h2.clearChildren();

        ImageTextButton lastButton = null;

        for (ImageTextButton btn : tabButtons.values()) {
            h2.add(btn).height(h2_height-2).pad(1).width(100).padLeft(10);
            lastButton = btn;
        }
        tabButtonAction(lastButton);
    }

    public void leaveLobby() {
        if (tabButtons.size() > 1) {
            removeCurrentTab();
        }
    }

    public void setFreeze(boolean frozen) {
        if (frozen == isFrozen)
            return;
        isFrozen = frozen;

        if (frozen) {
            for (ImageTextButton btn : tabButtons.values()) {
                if (btn.getStyle() == h2_btn_style_unfocused) {
                    btn.setStyle(h2_btn_style_frozen);
                    btn.setDisabled(true);
                }
            }
            return;
        }
        else {
            for (ImageTextButton btn : tabButtons.values()) {
                if (btn.getStyle() == h2_btn_style_frozen) {
                    btn.setStyle(h2_btn_style_unfocused);
                    btn.setDisabled(false);
                }
            }
        }
    }


    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        // Handle join request response
        for (LobbyJoinResponsePacket packet : packets_LobbyJoin) {
            if (packet.isHandled()) {
                packets_LobbyJoin.remove(packet);
            } else {
                packet.setHandled(true);

                Tab_Lobby tab = new Tab_Lobby(gsm, channel, new MapInfo(
                        packet.getLobbyName(),
                        packet.getMapFile().name,
                        packet.getHost(),
                        packet.getMapFile().description,
                        packet.getUsers().length,
                        8,
                        2,
                        8,
                        packet.getMapFile().mapDifficulty,
                        new TextureRegionDrawable(new TextureRegion(
                                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Map_Preview.png"))))
                ), packet.getHost().equals(RoboRally.clientInfo));


                for (int i = 0; i < packet.getUsers().length; i++) {
                    if (packet.getUsers()[i] != null) {
                        tab.addPlayer(i, packet.getUsers()[i]);
                    }
                }
                addTab(packet.getLobbyName(), tab, true);
            }
        }

        // Handle lobbies list packet
        for (LobbiesListPacket packet : packets_LobbyList) {
            if (packet.isHandled()) {
                packets_LobbyList.remove(packet);
            } else {
                packet.setHandled(true);

                Tab_Lobbies lobbies = ((Tab_Lobbies)tabs.get("Lobbies"));

                lobbies.lobbyButtons.clear();
                lobbies.lobbyViews.clear();
                lobbies.lobbies.clearChildren();
                lobbies.lobbies.add(lobbies.lobbiesHeader).row();


                for (LobbyInfo lobbyInfo : packet.getLobbies()) {
                    lobbies.addLobby(new MapInfo(
                            lobbyInfo.getLobbyName(),
                            lobbyInfo.getMapFile().name,
                            lobbyInfo.getHostName(),
                            lobbyInfo.getMapFile().description,
                            lobbyInfo.getNumPlayers(),
                            8,
                            2,
                            8,
                            lobbyInfo.getMapFile().mapDifficulty,
                            new TextureRegionDrawable(new TextureRegion(
                                    new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Map_Preview.png"))))
                    ));
                }
            }
        }

        // Handle updates
        for (LobbyUpdatePacket packet : packets_LobbyUpdates) {
            if (packet.isHandled()) {
                packets_LobbyUpdates.remove(packet);
            } else {
                packet.setHandled(true);
                ((Tab_Lobby)tabs.get(packet.getLobbyName())).update(packet);
            }
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(color_primary.r,color_primary.g, color_primary.b, color_primary.a);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
