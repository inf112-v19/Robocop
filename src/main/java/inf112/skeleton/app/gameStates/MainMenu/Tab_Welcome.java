package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;

public class Tab_Welcome extends MenuTab{
    HashMap<String, ImageTextButton> friends;
    Table friendsList;

    ImageTextButton.ImageTextButtonStyle fl_StyleOnline, fl_StyleOffline;

    // ts = Text space, fl = Friendslist
    private final int   ts_width        = 632,
                        padding_between = 40,
                        fl_width        = 382,
                        fl_tabHeight    = 45,
                        fl_headerHeight = 50;

    public Tab_Welcome(GameStateManager gsm, Channel channel) {
        super(gsm, channel);

        // Add Text Space (left half of the screen)
        Drawable textSpace = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/tmpScreen.png"))));
        display.add(new Image(textSpace)).size(ts_width, height);


        // Add Friendslist (right half of the screen)
        friendsList = new Table();
        friendsList.setSize(fl_width, height);
        friendsList.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Welcome/friendslist_bg.png")))));
        friendsList.top();

        // Friendslist header (Textbox: "Friendslist")
        friendsList.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Welcome/friendslist_header.png"))))))
            .size(fl_width, fl_headerHeight);
        friendsList.row();
        friendsList.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Welcome/friendslist_horizontalLine.png"))))))
                .size(fl_width-4, 1).padTop(1);
        friendsList.row();

        display.add(friendsList).size(fl_width, height).padLeft(padding_between);
        display.row();

        friends = new HashMap<>();

        Drawable tmp;

        // Add style for buttons representing online friends
        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Welcome/friendsButtonOnline.png"))));
        fl_StyleOnline = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, new BitmapFont());
        fl_StyleOnline.fontColor = Color.BLACK;
        fl_StyleOnline.font.getData().setScale(1.6f);

        // Add style for buttons representing offline friends
        tmp = new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Welcome/friendsButtonOffline.png"))));
        fl_StyleOffline = new ImageTextButton.ImageTextButtonStyle(tmp, tmp, tmp, new BitmapFont());
        fl_StyleOffline.fontColor = Color.BLACK;
        fl_StyleOffline.font.getData().setScale(1.6f);
    }

    public void addFriend(String name, boolean isOnline) {
        ImageTextButton friendButton = new ImageTextButton(name, isOnline ? fl_StyleOnline : fl_StyleOffline);
        friendButton.left().padLeft(55);
        friendsList.add(friendButton).size(fl_width-4, fl_tabHeight);
        friendsList.row();
        friends.put(name, friendButton);

        friendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                friendAction(((ImageTextButton) actor).getText().toString());
            }
        });
    }

    public void setFriendStatus(String name, boolean isOnline) {
        friends.get(name).setStyle(isOnline ? fl_StyleOnline : fl_StyleOffline);
    }

    public void friendAction(String name) {
        System.out.println(name);
    }
}
