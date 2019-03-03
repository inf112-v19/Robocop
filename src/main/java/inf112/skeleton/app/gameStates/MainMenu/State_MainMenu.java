package inf112.skeleton.app.gameStates.MainMenu;

// Source: https://youtu.be/24p1Mvx6KFg

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import inf112.skeleton.app.ChatGUI;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.app.Action.Action;
import inf112.skeleton.app.menu.Menu;
import inf112.skeleton.common.packet.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class State_MainMenu extends GameState {
    private Menu menu;
    private Channel channel;
    private Gson gson;

    public State_MainMenu(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        menu = new Menu();
        this.channel = channel;
        menu.add("Play", new Action() {
            public void invoke() {
                playGame("");
            }
            /*
            public void invoke() {
                // Get host IP from user and switch game-state.
                Gdx.input.getTextInput(new TextInputHandler() {
                    @Override
                    public void input(String ip) {
                        playGame(ip);
                    }
                }, "Host IP:", "", "xxx.xxx.xxx.xxx");
            }
            */
        });

        menu.add("Settings", new Action() {
            public void invoke() {
                System.out.println("Clicked settings-button");
            }
        });
        menu.add("End game", new Action() {
            public void invoke() {
                System.out.println("Clicked \"End game\"");
            }
        });
    }

    protected void playGame(String ip) {
        // TODO: Validate IP and connect
        System.out.println(ip);
        Random random = new Random();
        Packet packet = new Packet(0, new LoginPacket("" + random.nextInt(), "oiajwdioj"));
        gson = new Gson();
        System.out.println("sending: " + gson.toJson(packet));
        channel.writeAndFlush(gson.toJson(packet)+"\r\n");
    }


    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {
        menu.render(sb);
    }

    @Override
    public void dispose() {
        menu.dispose();
    }

    @Override
    public void focus() {
        menu.focus();
    }

    @Override
    public void resize(int width, int height) {
        menu.resize(width, height);
    }
}
