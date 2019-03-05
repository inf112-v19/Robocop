package inf112.skeleton.app.Socket;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Label;
import java.awt.Font;
import java.awt.TextField;
import javax.swing.JPasswordField;


import inf112.skeleton.app.ChatGUI;
import inf112.skeleton.common.packet.LoginPacket;
import inf112.skeleton.common.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ChatLogin {

    public JFrame frmChat;
    private JPasswordField passwordField;
    private TextField textField;

    public static Label label_3;
    static ChatLogin window;
    static Gson gson;
    /**
     * Launch the application.
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//
//                    window = new ChatLogin();
//                    window.frmChat.setVisible(true);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap bootstrap = new Bootstrap()
//                .group(group)
//                .channel(NioSocketChannel.class) //use new io sockets
//                .handler(new ChatLoginInitializer()); //handle all IncomingPacket messages
//
//        ChatGUI.channel = bootstrap.connect("localhost",58008).sync().channel(); // creating a connection with the server
    }

    /**
     * Create the application.
     */
    public ChatLogin() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmChat = new JFrame();
        frmChat.setTitle("Chat System test");
        frmChat.setBounds(100, 100, 450, 250);
        frmChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmChat.getContentPane().setLayout(null);

        Label label = new Label("Chat Login");
        label.setFont(new Font("Dialog", Font.PLAIN, 21));
        label.setAlignment(Label.CENTER);
        label.setBounds(10, 23, 414, 22);
        frmChat.getContentPane().add(label);

        textField = new TextField();
        textField.setBounds(194, 77, 107, 22);
        frmChat.getContentPane().add(textField);

        Label label_1 = new Label("Username:");
        label_1.setBounds(126, 77, 62, 22);
        frmChat.getContentPane().add(label_1);

        Label label_2 = new Label("Password:");
        label_2.setBounds(126, 128, 62, 22);
        frmChat.getContentPane().add(label_2);

        passwordField = new JPasswordField();
        passwordField.setBounds(194, 128, 107, 22);
        frmChat.getContentPane().add(passwordField);

        JButton btnNewButton = new JButton("Login");
        btnNewButton.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent arg0) {
                if (ChatGUI.channel.isOpen()) {
                    Packet packet = new Packet(0, new LoginPacket(textField.getText(),passwordField.getText()));
                    Map<String, String> colours = new HashMap<>();
                    colours.put("id", "0");
                    colours.put("username", textField.getText());
                    colours.put("password", passwordField.getText());
                    gson = new Gson();
                    System.out.println("sending: " + gson.toJson(packet));
                    ChatGUI.channel.writeAndFlush(gson.toJson(packet)+"\r\n");
//                    ChatGUI.channel.writeAndFlush("username:"+textField.getText()+"\r\n");
//                    ChatGUI.channel.writeAndFlush("password:"+passwordField.getText()+"\r\n");
                } else {

                }
            }
        });
        btnNewButton.setBounds(159, 177, 124, 23);
        frmChat.getContentPane().add(btnNewButton);

        label_3 = new Label("");
        label_3.setAlignment(Label.CENTER);
        label_3.setForeground(Color.RED);
        label_3.setBounds(57, 49, 322, 22);
        frmChat.getContentPane().add(label_3);
    }
}
