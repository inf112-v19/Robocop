package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Tab_Lobby extends MenuTab{
    LinkedHashMap<String, ImageTextButton> players;
    Table players_display;
    protected boolean isHost;
    public MapInfo mapInfo;

    ScrollableTextbox chat;

    ImageTextButton.ImageTextButtonStyle fl_StyleOnline, fl_StyleOffline, fl_StyleOpen, btn_StyleNormal, btn_StyleFrozen;
    ImageTextButton btn_start, btn_leave;

    // ts = Text space, pl = Players in lobby
    private final int   ts_width        = 632,
                        padding_between = 40,
                        pl_width        = 600,
                        pl_tabHeight    = 45,
                        pl_headerHeight = 50,
                        mp_width        = 250,
                        mp_height       = 150;

    public Tab_Lobby(GameStateManager gameStateManager, Channel ch, MapInfo mapinfo, boolean isHost) {
        super(gameStateManager, ch);
        this.isHost = isHost;
        this.mapInfo = mapinfo;

        players = new LinkedHashMap<>();

        players_display = new Table();
        players_display.setSize(pl_width, pl_tabHeight * 8);
        players_display.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobby/player_bg.png")))));
        players_display.top();

        Drawable tmp;

        // Add style for buttons representing online players
        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobby/player_Online.png"))));
        fl_StyleOnline = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, new BitmapFont());
        fl_StyleOnline.fontColor = Color.BLACK;
        fl_StyleOnline.font.getData().setScale(1.6f);

        // Add style for buttons representing offline players
        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobby/player_Offline.png"))));
        fl_StyleOffline = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, new BitmapFont());
        fl_StyleOffline.fontColor = Color.BLACK;
        fl_StyleOffline.font.getData().setScale(1.6f);

        fl_StyleOpen = new ImageTextButton.ImageTextButtonStyle();
        fl_StyleOpen.font = new BitmapFont();
        fl_StyleOpen.fontColor = Color.BLACK;
        fl_StyleOpen.font.getData().setScale(1.6f);

        addPlayer(RoboRally.username, true);



        chat = new ScrollableTextbox(5,((State_MainMenu)gsm.peek()).im, channel);
        chat.tableWidth = pl_width;
        chat.updateDisplay();


        Table leftHalf_display = new Table();
        leftHalf_display.setSize(pl_width, height);

        leftHalf_display.add(players_display).row();
        leftHalf_display.add(chat.display).width(pl_width);

        display.add(leftHalf_display);



        Table rightHalf_display = new Table();
        int rh_width = width - pl_width;
        rightHalf_display.setSize(mp_width, height);
        rightHalf_display.top();
        rightHalf_display.padLeft(120);

        display.top();

        Label.LabelStyle lbStyle = new Label.LabelStyle();
        lbStyle.font = new BitmapFont();
        lbStyle.fontColor = Color.BLACK;
        lbStyle.font.getData().setScale(1.6f);

        Label mapNameLabel = new Label(mapInfo.mapName, lbStyle);
        mapNameLabel.setAlignment(Align.center);

        rightHalf_display.add(mapNameLabel).width(mp_width).align(Align.center).top().row();
        rightHalf_display.add(new Image(mapInfo.minimap_preview)).size(mp_width, mp_height).align(Align.center).top().expandY().row();


        btn_StyleFrozen = ((State_MainMenu)gsm.peek()).h2_btn_style_frozen;
        btn_StyleNormal = ((State_MainMenu)gsm.peek()).h2_btn_style_focused;


        btn_start = new ImageTextButton("Start", isHost ? btn_StyleNormal : btn_StyleFrozen);
        if (isHost)
            btn_start.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    gsm.set(new State_Playing(gsm, channel));
                }
            });
        else btn_start.setDisabled(true);
        rightHalf_display.add(btn_start).padTop(20).size(100, 45).align(Align.right).row();


        btn_leave = new ImageTextButton("Leave", btn_StyleNormal);
        btn_leave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                ((State_MainMenu)gsm.peek()).removeCurrentTab();
                ((State_MainMenu)gsm.peek()).setFreeze(false);
            }
        });

        rightHalf_display.add(btn_leave).size(100, 45).padTop(2).padBottom(30).align(Align.right).row();



        display.add(rightHalf_display).width(mp_width);
    }

    public void addPlayer(String name, boolean isOnline) {
        players.put(name, new ImageTextButton(name, isOnline ? fl_StyleOnline : fl_StyleOffline));
        players_display.clearChildren();

        for (ImageTextButton btn : players.values())
            players_display.add(btn).size(pl_width, pl_tabHeight).row();

        for (int i = players.size() ; i < 8 ; i++)
            players_display.add(new ImageTextButton("Open", fl_StyleOpen)).size(pl_width, pl_tabHeight).row();
    }

    public void updatePlayer(String name, boolean isOnline) {
        players.get(name).setStyle(isOnline ? fl_StyleOnline : fl_StyleOffline);
    }

    public void removePlayer(String name) {
        players.remove(name);
        players_display.clearChildren();
        for (ImageTextButton btn : players.values()) {
            players_display.add(btn).size(pl_width, pl_tabHeight).row();
        }
    }
}
