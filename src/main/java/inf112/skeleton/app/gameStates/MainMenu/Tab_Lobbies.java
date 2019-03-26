package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.DataRequestPacket;
import inf112.skeleton.common.packet.data.JoinLobbyPacket;
import inf112.skeleton.common.packet.data.PacketData;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.specs.MapDifficulty;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Tab_Lobbies extends MenuTab {

    Table lobbies;

    Drawable lobbyView_bg;

    LinkedHashMap<String, ImageTextButton> lobbyButtons;
    LinkedHashMap<String, Table> lobbyViews;

    ImageTextButton.ImageTextButtonStyle lobbyButtonStyleFocused, lobbyButtonStyleUnfocused;
    ImageTextButton currentLobby;

    public Table lobbiesHeader;


    BitmapFont font;


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

    public Tab_Lobbies(GameStateManager gameStateManager, Channel ch) {
        super(gameStateManager, ch);
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        lobbyButtons = new LinkedHashMap<>();
        lobbyViews = new LinkedHashMap<>();

        // Set style of lobby buttons
        lobbyView_bg = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/LobbiesView_bg.png"))));

        Drawable tmp;

        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/LobbyButtonFocused.png"))));
        lobbyButtonStyleFocused = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, font);
        lobbyButtonStyleFocused.fontColor = Color.BLACK;

        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/LobbyButtonUnfocused.png"))));
        lobbyButtonStyleUnfocused = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, font);
        lobbyButtonStyleUnfocused.fontColor = Color.BLACK;


        // Add Lobbies table (Left side of screen)
        lobbies = new Table();
        lobbies.setSize(lb_width, height);
        lobbies.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_bg.png")))));
        lobbies.top();

        // Lobbies header (Textbox: "Lobbies"):
        lobbiesHeader = new Table();
        lobbiesHeader.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_header.png")))));
        lobbiesHeader.setSize(lb_width, lb_headerHeight);
        lobbiesHeader.right();

        ImageButton lobbyUpdate = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/updateButtonFocused.png")))));

        lobbyUpdate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Packet packet = new Packet(ToServer.REQUEST_DATA, new DataRequestPacket(DataRequest.LOBBY_LIST));
                channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
            }
        });

        ImageButton lobbyCreate = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/plusButtonFocused.png")))));

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
        lobbies.add(lobbiesHeader);
        lobbies.row();
        lobbies.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_horizontalLine.png"))))))
                .size(lb_width-4, 1).padTop(1);
        lobbies.row();

        display.add(lobbies).size(lb_width, height);



        // Request a list of all lobbies
        Packet packet = new Packet(ToServer.REQUEST_DATA, new DataRequestPacket(DataRequest.LOBBY_LIST));
        ch.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
    }

    public void addLobby(final MapInfo mapInfo) {
        // Create button which may be used to choose this map
        ImageTextButton btn_lobbyTab = new ImageTextButton(mapInfo.lobbyName, lobbyButtonStyleUnfocused);
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
                        currentLobby.setStyle(lobbyButtonStyleUnfocused);
                    currentLobby = (ImageTextButton)actor;
                    currentLobby.setStyle(lobbyButtonStyleFocused);
                }
            }
        });


        // Add map information displayed when button is clicked.
        Table map = new Table();
        map.setBackground(lobbyView_bg);
        map.setSize(lv_width, height);
        map.pad(10,5,5,5);
        map.columnDefaults(2);

        Label.LabelStyle txtStyle = new Label.LabelStyle(font, Color.BLACK);

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

        ImageTextButton btn_join = new ImageTextButton("Join", lobbyButtonStyleFocused);
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
}
