package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import inf112.skeleton.app.GUI.ChatBox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.DataRequestPacket;
import inf112.skeleton.common.packet.data.LobbyUpdatePacket;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

public class Tab_Lobby extends MenuTab{
    private String[]            playerNames;
    private ImageTextButton[]   playerButtons;
    private Table               players_display;

    private boolean isHost;
    private MapInfo mapInfo;

    private ImageTextButton.ImageTextButtonStyle fl_StyleOpen;

    // pl = Player-buttons, mp = mini-map
    private final int   pl_width        = 600,
                        pl_tabHeight    = 45,
                        mp_width        = 250,
                        mp_height       = 150;

    /**
     * Initialize lobby system containing preview of map, player list and buttons to start game or leave lobby
     * @param gameStateManager to manage game-states
     * @param ch to communicate with server
     * @param mapinfo to display preview of map
     * @param isHost whether the player was the one who created the lobby
     */
    public Tab_Lobby(GameStateManager gameStateManager, Channel ch, MapInfo mapinfo, boolean isHost) {
        super(gameStateManager, ch);
        this.isHost = isHost;
        this.mapInfo = mapinfo;

        playerNames = new String[8];
        playerButtons = new ImageTextButton[8];

        /*
         * Setup styles
         */

        // Style used for open player-slots.
        fl_StyleOpen = new ImageTextButton.ImageTextButtonStyle();
        fl_StyleOpen.font = RoboRally.graphics.default_font_1p6;
        fl_StyleOpen.fontColor = Color.BLACK;


        /*
         * Add friends-list and lobby chat-box to left part of tab.
         */

        players_display = new Table().top();
        players_display.setSize(pl_width, pl_tabHeight * 8);
        players_display.setBackground(RoboRally.graphics.lobby_playerList_bg);

        Table leftHalf_display = new Table();
        leftHalf_display.setSize(pl_width, height);
        leftHalf_display.add(players_display).row();
        leftHalf_display.add(new ChatBox(channel)).size(pl_width, height - players_display.getHeight());


        /*
         * Setup right side of tab:
         *     -> Map-name
         *     -> Map preview
         *     -> Button "Start"
         *     -> Button "Leave"
         */

        Table rightHalf_display = new Table().top().padLeft(120);
        rightHalf_display.setSize(mp_width, height);


        // Map name
        Label mapNameLabel = new Label(mapInfo.mapName, new Label.LabelStyle(RoboRally.graphics.default_font_1p6, Color.BLACK));
        mapNameLabel.setAlignment(Align.center);
        rightHalf_display.add(mapNameLabel).width(mp_width).align(Align.center).top().row();

        // Map preview
        rightHalf_display.add(new Image(mapInfo.minimap_preview)).size(mp_width, mp_height).align(Align.center).top().expandY().row();

        // Button "Start"
        ImageTextButton btn_start = new ImageTextButton("Start", isHost ? RoboRally.graphics.btnStyle_rounded_focused : RoboRally.graphics.btnStyle_rounded_frozen);
        if (isHost)
            btn_start.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    ToServer dataRequest = ToServer.REQUEST_DATA;
                    DataRequestPacket dataRequestPacket = new DataRequestPacket(DataRequest.LOBBY_START);
                    Packet packet = new Packet(dataRequest, dataRequestPacket);
                    channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
                }
            });
        else btn_start.setDisabled(true);
        rightHalf_display.add(btn_start).padTop(20).size(100, 45).align(Align.right).row();

        // Button "Leave"
        ImageTextButton btn_leave = new ImageTextButton("Leave", RoboRally.graphics.btnStyle_rounded_focused);
        btn_leave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                ToServer dataRequest = ToServer.REQUEST_DATA;
                DataRequestPacket dataRequestPacket = new DataRequestPacket(DataRequest.LOBBY_LEAVE);
                Packet packet = new Packet(dataRequest, dataRequestPacket);
                channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
            }
        });
        rightHalf_display.add(btn_leave).size(100, 45).padTop(2).padBottom(30).align(Align.right).row();



        display.add(leftHalf_display);
        display.top();
        display.add(rightHalf_display).width(mp_width);
    }

    /**
     * Add a player to the lobby
     * @param index player-array position
     * @param name of player
     */
    public void addPlayer(int index, String name) {
        playerButtons[index] = new ImageTextButton(name, RoboRally.graphics.btnStyle_players[index]);
        playerNames[index] = name;

        players_display.clearChildren();

        for (int i = 0; i < playerNames.length; i++) {
            if (playerNames[i] == null) {
                players_display.add(new ImageTextButton("Open", fl_StyleOpen)).size(pl_width, pl_tabHeight).row();
            } else {
                players_display.add(playerButtons[i]).size(pl_width, pl_tabHeight).row();
            }
        }
    }

    /**
     * Update player list in lobby
     * @param packet containing players in lobby
     */
    public void update(LobbyUpdatePacket packet) {
        playerNames = new String[8];
        playerButtons = new ImageTextButton[8];

        for (int i = 0; i < packet.getUsers().length; i++) {
            if (packet.getUsers()[i] != null) {
                this.addPlayer(i, packet.getUsers()[i]);
            }
        }
    }
}
