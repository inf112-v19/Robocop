package inf112.skeleton.app;


import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Box;
import javax.swing.border.LineBorder;

import inf112.skeleton.app.Socket.ChatLogin;
import io.netty.channel.Channel;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatGUI {

    private JFrame frmLevsChatSystem;
    private static TextField textField;
    public static JTextArea textArea;
    public static Channel channel;
    public static ChatGUI window;
    public static ChatLogin login;
    /**
     * Launch the application.
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    window = new ChatGUI();
                    window.frmLevsChatSystem.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static List list;
    /**
     * Create the application.
     */
    public ChatGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmLevsChatSystem = new JFrame();
        frmLevsChatSystem.setResizable(false);
        frmLevsChatSystem.setTitle("Test");
        frmLevsChatSystem.setBounds(100, 100, 628, 300);
        frmLevsChatSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLevsChatSystem.getContentPane().setLayout(null);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        horizontalBox.setBounds(10, 34, 414, 177);
        frmLevsChatSystem.getContentPane().add(horizontalBox);

        textArea = new JTextArea();
        horizontalBox.add(textArea);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        horizontalBox.add(scrollPane);

        Button button = new Button("Send Message");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (textField.getText().length() <= 0)
                    return;
                if (channel.isOpen()) {
                    channel.writeAndFlush(textField.getText()+"\r\n");
                    textField.setText("");
                }
            }
        });
        button.setActionCommand("Send Message");
        button.setBounds(315, 228, 109, 22);
        frmLevsChatSystem.getContentPane().add(button);

        textField = new TextField();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (textField.getText().length() <= 0)
                        return;
                    if (channel.isOpen()) {
                        System.out.println("here");
                        channel.writeAndFlush(textField.getText()+"\r\n");
                        textField.setText("");
                    }
                }
            }
        });
        textField.setBounds(10, 227, 299, 23);
        frmLevsChatSystem.getContentPane().add(textField);

        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(new LineBorder(new Color(0, 0, 0), 3));
        verticalBox.setBounds(463, 34, 138, 216);
        frmLevsChatSystem.getContentPane().add(verticalBox);

        list = new List();
        verticalBox.add(list);

        Panel panel = new Panel();
        panel.setBackground(Color.BLACK);
        panel.setBounds(437, 0, 10, 271);
        frmLevsChatSystem.getContentPane().add(panel);

        Label label = new Label("Online Users");
        label.setBounds(496, 10, 87, 22);
        frmLevsChatSystem.getContentPane().add(label);

        Label label_1 = new Label("Chatbox");
        label_1.setBounds(189, 6, 62, 22);
        frmLevsChatSystem.getContentPane().add(label_1);
    }
}
