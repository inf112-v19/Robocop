package inf112.skeleton.app.gameStates.NewMainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;

public class Tab_Lobbies extends MenuTab {

    Table lobbies;

    Drawable lobbyView_bg;

    HashMap<String, ImageTextButton> lobbyButtons;
    HashMap<String, Table> lobbyViews;

    ImageTextButton currentLobby;


    // lb = Lobbies, lv = Lobby view
    private final int   lb_width = 382,
                        lb_tabHeight = 45,
                        lb_headerHeight = 50,
                        padding_between = 20,
                        lv_width = 652;

    public Tab_Lobbies(GameStateManager gsm, Channel channel) {
        super(gsm, channel);

        lobbyButtons = new HashMap<>();
        lobbyViews = new HashMap<>();

        // Add Lobbies (Left side of screen)
        lobbies = new Table();
        lobbies.setSize(lb_width, height);
        lobbies.setBackground(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_bg.png")))));
        lobbies.top();

        // Lobbies header (Textbox: "Lobbies"):
        lobbies.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_header.png"))))))
                .size(lb_width, lb_headerHeight);
        lobbies.row();
        lobbies.add(new Image(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("graphics/ui/MainMenu/Lobbies/Lobbies_horizontalLine.png"))))))
                .size(lb_width-4, 1).padTop(1);
        lobbies.row();

        // tmp
        display.add(lobbies).size(lb_width, height);
        display.row();
    }
}
