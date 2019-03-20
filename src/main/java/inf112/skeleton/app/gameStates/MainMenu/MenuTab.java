package inf112.skeleton.app.gameStates.MainMenu;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

public class MenuTab {
    protected GameStateManager gsm;
    protected Channel channel;
    public Table display;


    protected final int width           = 1054,
                        height          = 589;

    public MenuTab(GameStateManager gsm, Channel channel) {
        this.gsm = gsm;
        this.channel = channel;

        display = new Table();
        display.setSize(width, height);
    }
}
