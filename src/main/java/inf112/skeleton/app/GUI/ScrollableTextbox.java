package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.gson.Gson;
import inf112.skeleton.common.packet.ChatMessagePacket;
import inf112.skeleton.common.packet.IncomingPacket;
import inf112.skeleton.common.packet.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import io.netty.channel.Channel;

/*
 * Circular message-box.
 */
public class ScrollableTextbox {
    InputMultiplexer inputMultiplexer;
    TextField[] lines;
    int lineAmount = 0,
            lineLimit,
            displayFrom = 0,
            numFields = 5;

    Stage stage;
    Table display;

    ImageButton button_up;
    ImageButton button_down;

    TextField.TextFieldStyle txtStyle;
    TextField inputField;
    Actor emptyField;
    Channel channel;

    int tableWidth = 600,
            tableHeight = 140;
    Gson gson = new Gson();
    public static ScrollableTextbox textbox = null;


    public ScrollableTextbox(int limit, InputMultiplexer inputMultiplexer, Channel channel) {
        this.channel = channel;
        this.textbox = this;
        lineLimit = limit;
        this.inputMultiplexer = inputMultiplexer;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        inputMultiplexer.addProcessor(stage);

        init_inputField();
        init_scrollButtons();
        emptyField = new Actor();


        txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = new BitmapFont();
        txtStyle.fontColor = Color.YELLOW;

        lines = new TextField[limit];
        for (int i = 0; i < limit; i++) {
            lines[i] = new TextField("", txtStyle);
            lines[i].setDisabled(true);
        }

        display = new Table();
        display.setDebug(false);
        display.setSize(tableWidth, tableHeight);
        display.background(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("chatStyleOpac.png")))));
        stage.addActor(display);
        updateDisplay();
    }

    private void init_inputField() {
        inputField = new TextField("", new Skin(Gdx.files.internal("uiskin.json")));
        inputField.setMessageText("Type a message...");
        inputField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if ((key == '\r' || key == '\n')) {
                    String inputText = inputField.getText();
                    if (!inputText.equals("")) {
//                        push(inputText);
                        Packet packet = new Packet(IncomingPacket.CHAT_MESSAGE.ordinal(), new ChatMessagePacket(inputText));
                        channel.writeAndFlush(gson.toJson(packet) + "\r\n");

                    }
                    inputField.setText("");
                    stage.setKeyboardFocus(null);
                    Gdx.input.setInputProcessor(inputMultiplexer);
                }
            }
        });

        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getTarget() == inputField) {
                    Gdx.input.setInputProcessor(stage);
                } else {
                    Gdx.input.setInputProcessor(inputMultiplexer);
                    stage.setKeyboardFocus(null);
                }
                return false;
            }
        });
    }

    private void init_scrollButtons() {
        button_up = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlack.png")))));
        button_down = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlackRot.png")))));

        button_up.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        scrollDisplay(-1);
                    }
                }
        );
        button_down.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        scrollDisplay(1);
                    }
                }
        );
    }


    public void scrollDisplay(int scrollAmount) {
        if (scrollAmount < 0) {
            scrollAmount = -((-scrollAmount) % lineLimit);
        }
        displayFrom = (displayFrom + scrollAmount + lineLimit) % lineLimit;

        updateDisplay();
    }


    public void push(ChatMessagePacket chatMsg) {
        lines[++lineAmount % lineLimit].setText(chatMsg.getMessage());
        if (lineAmount % lineLimit == (displayFrom + 1) % lineLimit)
            scrollDisplay(1);
    }


    public void updateDisplay() {
        int arrowButtonSize = (tableHeight - 10) / numFields;

        display.clearChildren();

        // Add empty cells to the table.
        display.add(lines[(displayFrom - numFields + 2 + lineLimit) % lineLimit]).width(tableWidth - arrowButtonSize - 20).padLeft(10).padBottom(1).padTop(3).uniform();
        display.add(button_up).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
        for (int i = numFields - 3; i >= 0; i--) {
            display.add(lines[(displayFrom - i + lineLimit) % lineLimit]).width(tableWidth - arrowButtonSize - 20).padLeft(10).padBottom(1).uniform();
            display.add(emptyField);
            display.row();
        }

        display.add(inputField).width(tableWidth - arrowButtonSize - 30).height(arrowButtonSize - 4).padLeft(5).padBottom(4).uniform();
        display.add(button_down).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
    }

    public void render(Batch sb) {
        stage.draw();
    }

    public void setPosition(int x, int y) {
        display.setPosition(x, y);
    }
}
