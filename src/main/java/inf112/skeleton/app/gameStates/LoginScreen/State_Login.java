package inf112.skeleton.app.gameStates.LoginScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.common.packet.data.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

import static inf112.skeleton.common.status.LoginResponseStatus.NO_RESPONSE_YET;

public class State_Login extends GameState {
    private TextField usernameField, passwordField, messageToUser;

    public LoginResponseStatus loginStatus;
    private long loginRequestTime, loginRequestNextStop;

    public State_Login(GameStateManager gsm, Channel channel) {
        super(gsm, channel);

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Table loginDetails = new Table();
        Skin skin = RoboRally.graphics.default_skin;

        /*
         * RoboCop logo
         */

        loginDetails.add(new Image(RoboRally.graphics.logo)).size(500,190).padBottom(30).colspan(2).row();

        // Add a field to display error messages to user...
        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default-font");
        txtStyle.fontColor = Color.RED;

        messageToUser = new TextField("", txtStyle);
        messageToUser.setDisabled(true);
        messageToUser.setAlignment(Align.center);
        loginDetails.add(messageToUser).size(600, 30).colspan(2).row();


        /*
         * Username fields
         */

        TextField tmp;

        // Add "username" text-box.
        tmp = new TextField("Username", skin);
        tmp.setDisabled(true);
        loginDetails.add(tmp).right();

        // Add input-field for typing username.
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        loginDetails.add(usernameField).left().row();


        /*
         * Password fields
         */

        // Add "password" text-box.
        tmp = new TextField("Password", skin);
        tmp.setDisabled(true);
        loginDetails.add(tmp).right();

        // Add input-field for typing password.
        passwordField = new TextField("", skin);
        passwordField.setMessageText("********");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                // 10 = Keys.ENTER (Libgdx uses a different keyboard system)
                if ((int)c == 10)
                    enterLobby();
            }
        });
        loginDetails.add(passwordField).left().row();


        /*
         * Login button
         */

        tmp = new TextField("Login ", skin);
        tmp.setDisabled(true);
        tmp.setAlignment(Align.center);
        tmp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                enterLobby();
            }
        });
        loginDetails.add(tmp).colspan(2).center().padTop(5).size(300, 40).row();


        /*
         * Register button
         */

        tmp = new TextField("Register", skin);
        tmp.setDisabled(true);
        tmp.setAlignment(Align.center);
        tmp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked register button...");
                enterLobby();
            }
        });
        loginDetails.add(tmp).colspan(2).center().padTop(2).width(300).row();


        loginDetails.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f + 100, Align.center);

        stage.addActor(loginDetails);
        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(usernameField);
    }

    private String getUsername() {
        return usernameField.getText();
    }

    private String getPassword() {
        return passwordField.getText();
    }

    private void notifyUser(Color color, String text) {
        messageToUser.getStyle().fontColor = color;
        messageToUser.setText(text);
    }

    private void notifyUser(String text) {
        notifyUser(Color.YELLOW, text);
    }

    // Send login request to server and change game-state if login successful
    private void enterLobby() {
        String  username = getUsername(),
                password = getPassword();

        // If username is too short, notify user
        if (username.length() < 3) {
            stage.setKeyboardFocus(usernameField);
            notifyUser(Color.RED, "Username must have at least 3 characters...");
            return;
        }

        // If password is too short, notify user
        if (password.length() < 5) {
            stage.setKeyboardFocus(passwordField);
            notifyUser(Color.RED, "Password must have at least 5 characters...");
            return;
        }

        // Send login request and wait for a response (Will be handled in update)
        channel.writeAndFlush(Tools.GSON.toJson(new Packet(0, new LoginPacket(username, password)))+"\r\n");

        loginStatus = NO_RESPONSE_YET;
        loginRequestTime = System.currentTimeMillis();
        loginRequestNextStop = 0;

        // Disable input while waiting for login response
        Gdx.input.setInputProcessor(null);
        stage.setKeyboardFocus(null);
    }

    @Override
    public void update(float dt) {
        if (loginStatus == null)
            return;

        switch(loginStatus) {
            case NO_RESPONSE_YET:
                // If user has clicked anywhere, remove message to user and cancel login-request (After switch-case).
                if (Gdx.input.isTouched()) {
                    notifyUser("");
                    break;
                }

                // Update message to user every second while waiting for login response from server
                if (System.currentTimeMillis() >= loginRequestTime + loginRequestNextStop * 1000) {
                    messageToUser.setText("Logging in... (waited " + loginRequestNextStop + " seconds)");
                    loginRequestNextStop++;
                }
                return;

            case LOGIN_SUCCESS:
                RoboRally.username = getUsername();
                gsm.set(new State_MainMenu(gsm, channel));
                return;

            case ALREADY_LOGGEDIN:
                notifyUser(Color.RED, "User already logged in");
                break;

            case WRONG_LOGINDETAILS:
                notifyUser(Color.RED, "Wrong username or password");
                break;

            default:
                notifyUser("Unhandled login-status: " + loginStatus.name());
                break;
        }

        // Enable input.
        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(messageToUser);
        loginStatus = null;
    }

    @Override
    public void render(SpriteBatch sb) {
        Color bgColor = RoboRally.graphics.color_primary;
        Gdx.gl.glClearColor(bgColor.r,bgColor.g, bgColor.b, bgColor.a);
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
