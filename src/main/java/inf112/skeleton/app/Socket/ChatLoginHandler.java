package inf112.skeleton.app.Socket;


import inf112.skeleton.app.ChatGUI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatLoginHandler extends SimpleChannelInboundHandler<String> {

    private String[] args = { "",""};

    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
        System.out.println(arg1);
        if (arg1.equalsIgnoreCase("loginsuccess")) {
            ChatGUI.main(args);

            ChatLogin.window.frmChat.dispose();
        } else if (arg1.equalsIgnoreCase("wrongpassword")){
            ChatLogin.label_3.setText("Invalid password, try again!");
        } else if (arg1.equalsIgnoreCase("loggedinalready"))
            ChatLogin.label_3.setText("This account is already logged in!");
        else {
            if (arg1.startsWith("list:")) {
                ChatGUI.list.add(arg1.replaceAll("list:", ""));
            } else if (arg1.startsWith("listremove:")) {
                ChatGUI.list.remove(arg1.replaceAll("listremove:", ""));
            } else
                ChatGUI.textArea.append(arg1+"\n");
        }
    }
}