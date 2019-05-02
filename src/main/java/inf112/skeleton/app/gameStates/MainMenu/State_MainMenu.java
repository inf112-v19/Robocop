package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.gameStates.GameMessage.State_Message;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.DataRequestPacket;
import inf112.skeleton.common.packet.data.LobbiesListPacket;
import inf112.skeleton.common.packet.data.LobbyJoinResponsePacket;
import inf112.skeleton.common.packet.data.LobbyUpdatePacket;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.specs.LobbyInfo;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class State_MainMenu extends GameState {
    public InputMultiplexer im;
    private Table           h2,
                            main;

    private LinkedHashMap<String, MenuTab>          tabs;
    private LinkedHashMap<String, ImageTextButton>  tabButtons;
    private ImageTextButton                         currentTab;

    public Queue<LobbyJoinResponsePacket>   packets_LobbyJoin = new ConcurrentLinkedQueue<>();
    public Queue<LobbiesListPacket>         packets_LobbyList = new ConcurrentLinkedQueue<>();
    public Queue<LobbyUpdatePacket>         packets_LobbyUpdates = new ConcurrentLinkedQueue<>();
    public Queue<Boolean>                   packets_GameStart = new ConcurrentLinkedQueue<>();


    private final int   h1_height           = 60,
                        h2_height           = 45,
                        main_height         = 615,
                        main_padding        = 13;
    private boolean     isFrozen = false;

    /**
     * Initialize main menu
     * @param gameStateManager handles game-states
     * @param ch channel to communicate with server
     */
    public State_MainMenu(GameStateManager gameStateManager, Channel ch) {
        super(gameStateManager, ch);
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        im = new InputMultiplexer();

        // Data-structures for storing tabs and tab-buttons
        tabs = new LinkedHashMap<>();
        tabButtons = new LinkedHashMap<>();

        /*
         * Main header (Text: "RoboCop")
         */

        Table h1 = new Table().left();
        h1.setBackground(RoboRally.graphics.mainMenu_h1);
        h1.setSize(stage.getWidth(), h1_height);


        /*
         * Second header (ImageTextButtons: Name of each tab (Contained in h2), "Logout")
         */

        Table h2_full = new Table();
        h2_full.setSize(stage.getWidth(), h2_height);
        h2_full.setBackground(RoboRally.graphics.mainMenu_h2);
        h2_full.add().width(0).padLeft(5);

        // Table of buttons representing each tab
        h2 = new Table().left();
        h2.setSize(stage.getWidth() - 113, h2_height);

        // Logout button
        ImageTextButton logoutButton = new ImageTextButton("Logout", RoboRally.graphics.btnStyle_rounded_focused);
        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gsm.set(new State_Login(gsm, channel));
                DataRequestPacket logoutCMD = new DataRequestPacket(DataRequest.LOG_OUT);
                Packet packet = new Packet(ToServer.REQUEST_DATA, logoutCMD);
                packet.sendPacket(channel);
            }
        });

        h2_full.add(h2).size(stage.getWidth() - 113, h2_height);
        h2_full.add(logoutButton).size(100, h2_height-2).row();


        /*
         * Main menu body: Tab-contents should be put here...
         */

        main = new Table().left();
        main.setBackground(RoboRally.graphics.mainMenu_body);
        main.setSize(stage.getWidth(), main_height);


        /*
         * Put everything together.
         */

        // Add default tabs:
        addTab("Lobbies", new Tab_Lobbies(gsm, channel), false);

        // Screen display
        Table display = new Table();
        display.setSize(stage.getWidth(), stage.getHeight());
        display.add(h1).expand().height(h1_height).row();
        display.add(h2_full).size(stage.getWidth(), h2_height).row();
        display.add(main).expand().row();

        // Add display to stage and set input handler.
        stage.addActor(display);
        Gdx.input.setInputProcessor(im);
        im.addProcessor(stage);


        // trash client data
        RoboRally.gameBoard = null;
    }

    /**
     * What happens when a tab-button is clicked
     * @param textButton which tab-button was clicked
     */
    private void tabButtonAction (ImageTextButton textButton) {
        // If button is already focused, return.
        if (currentTab == textButton) {
            return;
        }

        // Set old tab style: unfocused.
        if (currentTab != null) {
            currentTab.setStyle(RoboRally.graphics.btnStyle_rounded_unfocused);
        }

        // Update tab and set style: focused.
        currentTab = textButton;
        currentTab.setStyle(RoboRally.graphics.btnStyle_rounded_focused);

        // Update menu body to display the tab
        main.clearChildren();
        main.add(tabs.get(currentTab.getText().toString()).display).pad(main_padding);
        main.row();
    }

    /**
     * Add a new tab to the menu
     * @param tabName what's displayed on the tab-button
     * @param tab what to show when tab-button clicked
     * @param goTo whether to force enter this tab
     */
    public void addTab(String tabName, MenuTab tab, boolean goTo) {
        // Add tab-button to second header.
        ImageTextButton newTabButton = new ImageTextButton(tabName, RoboRally.graphics.btnStyle_rounded_unfocused);
        h2.add(newTabButton).height(h2_height-2).pad(1).width(150).padLeft(10);

        // Keep track of the new tab and tab-button.
        tabButtons.put(tabName, newTabButton);
        tabs.put(tabName, tab);

        // Add event-listener to button (What happens when clicked)
        newTabButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                tabButtonAction((ImageTextButton) actor);
            }
        });

        // Focus tab button and display tab.
        if (currentTab == null || goTo)
            tabButtonAction(newTabButton);
    }

    /**
     * Removes the current tab from the menu.
     */
    public void removeCurrentTab() {
        String removeName = currentTab.getText().toString();
        tabButtons.remove(removeName);
        tabs.remove(removeName);

        h2.clearChildren();

        ImageTextButton lastButton = null;

        for (ImageTextButton btn : tabButtons.values()) {
            h2.add(btn).height(h2_height-2).pad(1).width(150).padLeft(10);
            lastButton = btn;
        }
        tabButtonAction(lastButton);
    }

    /**
     * Remove tab-button from menu (Will always be the current tab.
     */
    public void leaveLobby() {
        if (tabButtons.size() > 1) {
            removeCurrentTab();
        }
    }

    /**
     * Disable/enable the functionality to switch between menu tabs and changes tab-button appearances.
     * @param frozen whether to disable or enable switch-functionality.
     */
    public void setFreeze(boolean frozen) {
        if (frozen == isFrozen)
            return;
        isFrozen = frozen;

        if (frozen) {
            for (ImageTextButton btn : tabButtons.values()) {
                if (btn.getStyle() == RoboRally.graphics.btnStyle_rounded_unfocused) {
                    btn.setStyle(RoboRally.graphics.btnStyle_rounded_frozen);
                    btn.setDisabled(true);
                }
            }
        }
        else {
            for (ImageTextButton btn : tabButtons.values()) {
                if (btn.getStyle() == RoboRally.graphics.btnStyle_rounded_frozen) {
                    btn.setStyle(RoboRally.graphics.btnStyle_rounded_unfocused);
                    btn.setDisabled(false);
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        // TODO: Code cleanup
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
                        RoboRally.graphics.getDrawable(RoboRally.graphics.folder_MainMenu + "Lobbies/Map_Preview.png")
                ), packet.getHost().equals(RoboRally.clientInfo));


                for (int i = 0; i < packet.getUsers().length; i++) {
                    if (packet.getUsers()[i] != null) {
                        tab.addPlayer(i, packet.getUsers()[i]);
                    }
                }
                addTab(packet.getLobbyName(), tab, true);
                setFreeze(true);
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

                boolean lobbyFound = false;

                for (LobbyInfo lobbyInfo : packet.getLobbies()) {
                    if(lobbies.currentLobby!= null) {
                        if(lobbies.currentLobby.getText().toString().equals(lobbyInfo.getLobbyName())){
                            lobbyFound = true;
                        }
                    }
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
                            RoboRally.graphics.getDrawable(RoboRally.graphics.folder_MainMenu + "Lobbies/Map_Preview.png")
                    ));
                }
                if(!lobbyFound && lobbies.currentLobby != null){
                    lobbies.removeDetails();
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
        if (packets_GameStart.size() > 0) {
            packets_GameStart = new ConcurrentLinkedQueue<>();
            gsm.set(new State_Playing(gsm, channel));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Color bgColor = RoboRally.graphics.color_primary;
        Gdx.gl.glClearColor(bgColor.r,bgColor.g, bgColor.b, bgColor.a);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
