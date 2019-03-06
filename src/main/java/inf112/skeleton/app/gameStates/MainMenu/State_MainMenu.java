package inf112.skeleton.app.gameStates.MainMenu;

// Source: https://youtu.be/24p1Mvx6KFg

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.gson.Gson;
import inf112.skeleton.app.ChatGUI;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.app.Action.Action;
import inf112.skeleton.app.GUI.Menu;
import inf112.skeleton.common.packet.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.status.LoginResponseStatus;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

import static inf112.skeleton.common.status.LoginResponseStatus.NO_RESPONSE_YET;

public class State_MainMenu extends GameState {
    private Menu menu;
    private Channel channel;
    private Gson gson;
    public LoginResponseStatus loginStatus;

    InputMultiplexer inputMultiplexer;
    TextField inputField;
    String username;

    public State_MainMenu(GameStateManager gsm, Channel channel) {
        super(gsm, channel);

        if (channel == null)
            throw new IllegalArgumentException("<State_MainMenu>: Error upon initialization. Second argument \"channel\" input is null");

        this.channel = channel;
        username = "";

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        menu = new Menu();
        menu.add("Play", new Action() {
            public void invoke() {
                playGame("");
            }
        });

        menu.add("Settings", new Action() {
            public void invoke() {
                System.out.println("Clicked settings-button");
            }
        });
        menu.add("End game", new Action() {
            public void invoke() {
                Gdx.app.exit();
            }
        });


        Skin inputFieldSkin = new Skin(Gdx.files.internal("uiskin.json"));

        inputField = new TextField("", inputFieldSkin);
        inputField.setMessageText("Username");
        inputField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                username = inputField.getText();
                if ((key == '\r' || key == '\n')) {
                    menu.stage.setKeyboardFocus(null);
                    playGame("");
                }
            }
        });


        Table usernameTab = new Table();
        TextField   usernameCellLeft = new TextField("Username: ", inputFieldSkin);
        usernameCellLeft.setDisabled(true);

        usernameTab.add(usernameCellLeft).width(100).height(40);
        usernameTab.add(inputField).width(200).height(40);
        usernameTab.row();

        //usernameTab.setFillParent(true);
        usernameTab.center();
        usernameTab.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2+112);

        menu.stage.addActor(usernameTab);
        gson = new Gson();
    }

    protected void playGame(String ip) {
        // TODO: Validate IP and connect
        System.out.println(ip);
        String packetData = gson.toJson(new Packet(0, new LoginPacket(username, "oiajwdioj")));
        System.out.println("sending: " + packetData);

        loginStatus = NO_RESPONSE_YET;
        channel.writeAndFlush(packetData+"\r\n");

        long i = 0, j = 1;
        while(loginStatus == NO_RESPONSE_YET) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                if (++i == j) {
                    j <<= 1;
                    System.out.println("State_MainMenu <playGame>: Logging in... (waited " + (i / 10.0f) + " seconds)");
                }
            } catch (InterruptedException e) {
            }
        }
        switch(loginStatus) {
            case LOGIN_SUCCESS:
                RoboRally.username = username;
                gsm.set(new State_Playing(gsm, channel));
                break;
            case ALREADY_LOGGEDIN:
                System.out.println("User already logged in");
                break;
        }
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
        menu.focus(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        menu.resize(width, height);
    }
}
