package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.DataRequestPacket;
import inf112.skeleton.common.packet.data.JoinLobbyPacket;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

import java.util.LinkedHashMap;

public class Tab_Lobbies extends MenuTab {
    Table lobbies;
    Table lobbiesHeader;

    LinkedHashMap<String, ImageTextButton> lobbyButtons;
    LinkedHashMap<String, Table> lobbyViews;

    ImageTextButton currentLobby;

    // lb = Lobbies, lv = Lobby view, mp = mini-map
    private final int   lb_width = 382,
                        lb_tabHeight = 45,
                        lb_headerHeight = 50,
                        padding_between = 20,
                        lv_width = 652,
                        lv_tabHeight = 35,
                        lv_headerHeight = 50,
                        mp_width = 250,
                        mp_height = 150,
                        mp_posX = 360;

    /**
     * Initialize new tab with a list of lobbies to enter and a lobby-preview.
     * @param gameStateManager
     * @param ch
     */
    public Tab_Lobbies(GameStateManager gameStateManager, Channel ch) {
        super(gameStateManager, ch);

        lobbyButtons = new LinkedHashMap<>();
        lobbyViews = new LinkedHashMap<>();

        // Add Lobbies table (Left side of screen)
        lobbies = new Table().top();
        lobbies.setSize(lb_width, height);
        lobbies.setBackground(RoboRally.graphics.lobbies_bg);

        // Lobbies header (Textbox: "Lobbies"):
        lobbiesHeader = new Table().right();
        lobbiesHeader.setBackground(RoboRally.graphics.lobbies_header);
        lobbiesHeader.setSize(lb_width, lb_headerHeight);

        // Update-button: When clicked, a request is sent to the server to get the lobbies list.
        ImageButton lobbyUpdate = new ImageButton(RoboRally.graphics.btn_update);
        lobbyUpdate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Packet(ToServer.REQUEST_DATA, new DataRequestPacket(DataRequest.LOBBY_LIST)).sendPacket(channel);
            }
        });

        // Add-button: When clicked, a new tab is created, allowing the player to create a new lobby.
        ImageButton lobbyCreate = new ImageButton(RoboRally.graphics.btn_add);
        lobbyCreate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                State_MainMenu menu = ((State_MainMenu)gsm.peek());
                menu.addTab("Create lobby", new Tab_CreateLobby(gsm, channel), true);
                menu.setFreeze(true);
            }
        });

        lobbiesHeader.add(lobbyUpdate).size(40,40).right().padRight(10);
        lobbiesHeader.add(lobbyCreate).size(40,40).right().padRight(10);
        lobbies.add(lobbiesHeader).row();
        lobbies.add(new Image(RoboRally.graphics.lobbies_horizontal_line)).size(lb_width-4, 1).padTop(1).row();

        display.add(lobbies).size(lb_width, height);

        // Request a list of all lobbies
        new Packet(ToServer.REQUEST_DATA, new DataRequestPacket(DataRequest.LOBBY_LIST)).sendPacket(channel);
    }

    /**
     * Add a lobby-button to the lobbies-list and create a new lobby-preview.
     * @param mapInfo containing information about the lobby/map.
     */
    public void addLobby(final MapInfo mapInfo) {
        // Create button which may be used to choose this map
        ImageTextButton btn_lobbyTab = new ImageTextButton(mapInfo.lobbyName, RoboRally.graphics.btnStyle_lobbies_entry_unfocused);
        btn_lobbyTab.padLeft(20).left();
        lobbyButtons.put(mapInfo.lobbyName, btn_lobbyTab);
        lobbies.add(btn_lobbyTab).size(lb_width-4, lb_tabHeight);
        lobbies.row();
        btn_lobbyTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (currentLobby != actor) {
                    // Display map information
                    display.clearChildren();
                    display.add(lobbies).padRight(padding_between);
                    display.add(lobbyViews.get(((ImageTextButton)actor).getText().toString()))
                            .size(lv_width, height).left();

                    // Change button-focus
                    if (currentLobby != null)
                        currentLobby.setStyle(RoboRally.graphics.btnStyle_lobbies_entry_unfocused);
                    currentLobby = (ImageTextButton)actor;
                    currentLobby.setStyle(RoboRally.graphics.btnStyle_lobbies_entry_focused);
                }
            }
        });


        // Add map information displayed when button is clicked.
        Table map = new Table();
        map.setBackground(RoboRally.graphics.lobbies_bg);
        map.setSize(lv_width, height);
        map.pad(10,5,5,5);
        map.columnDefaults(2);

        Label.LabelStyle txtStyle = new Label.LabelStyle(RoboRally.graphics.default_font_1p5, Color.BLACK);

        // Force wanted table width to center header + add header
        map.add().size(lv_width-10, 0).colspan(2).row();
        map.add(new Label(mapInfo.lobbyName, txtStyle)).height(lv_headerHeight).colspan(3).row();


        // Add general information about the lobby
        Table generalInfo = new Table();

        generalInfo.top().left();
        generalInfo.add(new Label("Map: " + mapInfo.mapName, txtStyle)).height(lv_tabHeight).left().row();
        generalInfo.add(new Label("Host: " + mapInfo.hostName, txtStyle)).height(lv_tabHeight).left().row();
        generalInfo.add(new Label("Players: " + mapInfo.numPlayers + "/" + mapInfo.maxPlayers, txtStyle)).height(lv_tabHeight).left().row();
        generalInfo.add(new Label("Recommended players: " + mapInfo.minRecommendedPlayers + "-" + mapInfo.maxRecommendedPlayers, txtStyle)).height(lv_tabHeight).left().row();
        generalInfo.add(new Label("Level: " + mapInfo.difficulty.name(), txtStyle)).height(lv_tabHeight).left().row();

        map.top().left();
        map.add(generalInfo).size(mp_posX, height/3).left();

        // Add minimap
        map.add(new Image(mapInfo.minimap_preview)).size(mp_width, mp_height).left().top().row();


        // Add description
        map.add(new Label("Description:", txtStyle)).height(lv_tabHeight).left().colspan(2).row();
        map.add(new Label(mapInfo.description, txtStyle)).colspan(2).left().expandY().row();


        // Add join-button

        ImageTextButton btn_join = new ImageTextButton("Join", RoboRally.graphics.btnStyle_lobbies_entry_focused);
        btn_join.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                JoinLobbyPacket jp = new JoinLobbyPacket(mapInfo.lobbyName);
                Packet packet = new Packet(ToServer.JOIN_LOBBY, jp);
                channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
            }
        });
        map.add(btn_join).center().colspan(2).row();

        lobbyViews.put(mapInfo.lobbyName, map);
    }

    /**
     * Remove current lobby preview.
     */
    public void removeDetails() {
        currentLobby = null;
        display.clearChildren();
        display.add(lobbies).padRight(padding_between);
    }
}
