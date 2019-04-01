package inf112.skeleton.app.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.data.ChatMessagePacket;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.Packet;
import io.netty.channel.Channel;


public class ScrollableTextbox extends Table{
    private Label[] lines;
    private int     lineAmount = 0,
                    lineLimit,
                    displayFrom = 0,
                    tableWidth = 600,
                    tableHeight = 140;

    private ImageButton button_up,
                        button_down;

    private TextField inputField;
    private Actor emptyField;
    private Channel channel;

    public static ScrollableTextbox textbox = null;

    /**
     * Initializes scrollable textbox
     * @param limit number of messages to store
     * @param channel to communicate with server
     */
    public ScrollableTextbox(int limit, Channel channel){
        super();
        lineLimit = limit;
        this.channel = channel;
        this.textbox = this;

        setSize(tableWidth, tableHeight);
        background(RoboRally.graphics.chatBox_bg);

        init_inputField();
        init_scrollButtons();
        emptyField = new Actor();

        // Set default style of label and enable multicolor text.
        Label.LabelStyle txtStyle = new Label.LabelStyle();
        txtStyle.font = RoboRally.graphics.default_markup_font;
        txtStyle.fontColor = Color.YELLOW;

        lines = new Label[limit];
        for (int i = 0; i < limit; i++) {
            lines[i] = new Label("", txtStyle);
        }

        updateDisplay();
    }

    /**
     * Initialize input-field of text-box.
     */
    private void init_inputField() {
        inputField = new TextField("", RoboRally.graphics.default_skin);
        inputField.setMessageText(RoboRally.username + ": ");

        // Send message to server if enter key typed.
        inputField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if ((key == '\r' || key == '\n')) {
                    String inputText = inputField.getText();
                    if (!inputText.equals("")) {
                        new Packet(ToServer.CHAT_MESSAGE.ordinal(), new ChatMessagePacket(inputText)).sendPacket(channel);
                    }
                    inputField.setText("");
                }
            }
        });
    }

    /**
     * Add buttons to text-box, which may be used in order to view older/newer messages.
     */
    private void init_scrollButtons() {
        button_up = new ImageButton(RoboRally.graphics.btn_up);
        button_down = new ImageButton(RoboRally.graphics.btn_down);

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

    /**
     * View older/newer messages.
     * @param scrollAmount number of messages to scroll past.
     */
    private void scrollDisplay(int scrollAmount) {
        if (scrollAmount < 0) {
            scrollAmount = -((-scrollAmount) % lineLimit);
        }
        displayFrom = (displayFrom + scrollAmount + lineLimit) % lineLimit;

        updateDisplay();
    }

    /**
     * Add a new message to the scrollable text-box.
     * @param chatMsg package containing the new message
     */
    public void push(ChatMessagePacket chatMsg) {
        lines[++lineAmount % lineLimit].setText(chatMsg.getMessage());
        if (lineAmount % lineLimit == (displayFrom + 1) % lineLimit)
            scrollDisplay(1);
    }

    /**
     * Remove all messages from the visible part of the text-box and draw the messages which should be here now.
     */
    private void updateDisplay() {
        int numFields = 5,
            arrowButtonSize = (tableHeight - 10) / numFields;

        clearChildren();

        // Add first message to be displayed, in addition to the up-button.
        add(lines[(displayFrom - numFields + 2 + lineLimit) % lineLimit]).width(tableWidth - arrowButtonSize - 20).padLeft(10).padBottom(1).padTop(3).uniform();
        add(button_up).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        row();

        // Add all but the last message to be displayed.
        for (int i = numFields - 3; i >= 0; i--) {
            add(lines[(displayFrom - i + lineLimit) % lineLimit]).width(tableWidth - arrowButtonSize - 20).padLeft(10).padBottom(1).uniform();
            add(emptyField);
            row();
        }

        // Add the final message to be displayed, in addition to the down-button.
        add(inputField).width(tableWidth - arrowButtonSize - 30).height(arrowButtonSize - 4).padLeft(5).padBottom(4).uniform();
        add(button_down).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        row();
    }
}
