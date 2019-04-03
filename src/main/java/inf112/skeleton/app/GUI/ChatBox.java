package inf112.skeleton.app.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.ChatMessagePacket;
import io.netty.channel.Channel;

public class ChatBox extends Table {
    Channel channel;

    private TextField inputField;
    private Table messages;
    public ScrollPane scrollPane;

    private final int   width = 400,
                        height = 190,
                        height_input = 40,
                        padTop = 8,
                        padRight = 8;

    private Image black_box;
    public static ChatBox chatBox = null;

    /**
     * Initialize chat-box
     * @param channel to communicate with server.
     */
    public ChatBox(Channel channel) {
        super();
        this.channel = channel;
        chatBox = this;
        setSize(width, height);

        black_box = new Image(RoboRally.graphics.pixel_black);

        background(RoboRally.graphics.getDrawable(RoboRally.graphics.folder_ChatBox + "bg.png"));
        init_inputField();
        init_messageField();

        add(scrollPane).size(width - padRight, height - height_input - padTop).padTop(padTop).padRight(padRight).row();
        add(black_box).size(width - padRight, 1).padRight(padRight).row();
        add(inputField).size(width, height_input).bottom().row();
    }

    /**
     * Whenever parent changes size of chat-box, re-add all elements using given size.
     */
    @Override
    protected void sizeChanged() {
        clearChildren();
        add(scrollPane).padTop(padTop).padRight(padRight).size(getWidth() - padRight, getHeight() - height_input - padTop).row();
        add(black_box).size(getWidth() - padRight, 1).padRight(padRight).row();
        add(inputField).size(getWidth(), height_input).row();
    }

    /**
     * Initialize the input field.
     */
    private void init_inputField() {
        inputField = new TextField("", RoboRally.graphics.chatBox_skin);
        inputField.getStyle().cursor = RoboRally.graphics.chatBox_skin.newDrawable("cursor", Color.YELLOW);
        inputField.setMessageText("Username: Press ENTER to chat.");

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
     * Initialize the message field (where all messages are displayed).
     */
    private void init_messageField() {
        messages = new Table().left().bottom();

        scrollPane = new ScrollPane(messages, RoboRally.graphics.chatBox_skin);
        scrollPane.setForceScroll(false, true);
        scrollPane.setFlickScroll(true);
        scrollPane.setOverscroll(false,true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();
    }

    /**
     * Add a message to the message-field.
     * @param chatMsg packet containing chat message.
     */
    public void addMessage(ChatMessagePacket chatMsg) {
        Label messageLabel = new Label(chatMsg.getMessage(), RoboRally.graphics.labelStyle_markup_enabled);
        messageLabel.setAlignment(Align.left, Align.left);

        messages.add(messageLabel).height(RoboRally.graphics.labelStyle_markup_enabled.font.getLineHeight()).left().row();
        scrollPane.layout();
        scrollPane.scrollTo(0,0,0,0);
    }
}
