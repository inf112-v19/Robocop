package inf112.skeleton.app.Socket;


import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import inf112.skeleton.app.ChatGUI;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.LoginResponsePacket;
import inf112.skeleton.common.packet.OutgoingPacket;
import inf112.skeleton.common.status.LoginResponseStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatLoginHandler extends SimpleChannelInboundHandler<String> {

    private String[] args = {"", ""};
    private Gson gson = new Gson();
    private RoboRally game;

    public ChatLoginHandler(RoboRally game){
        this.game = game;
        game.setSocketHandler(this);
    }


    public void handleIncomingPacket(JsonObject jsonObject) throws Exception {
        OutgoingPacket packetId = OutgoingPacket.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                LoginResponsePacket responsePacket = gson.fromJson(jsonObject.get("data"), LoginResponsePacket.class);
                switch (LoginResponseStatus.values()[responsePacket.getStatusCode()]) {
                    case LOGIN_SUCCESS:
//                        ChatGUI.main(args);

//                        ChatLogin.window.frmChat.dispose();
                        break;
                    case ALREADY_LOGGEDIN:
                        ChatLogin.label_3.setText(responsePacket.getResponseMsg());
                        break;
                }
                break;
            case INIT_PLAYER:
                System.out.println("create player");
                break;
            default:
//                ChatGUI.textArea.append("resp: " + jsonObject.get("data") + "\n");
                break;

        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
        System.out.println(arg1);
        if (arg1.startsWith("{")) {
//                GsonBuilder gsonBuilder = new GsonBuilder();
//                gsonBuilder.registerTypeAdapter(PacketReciever.class, new PacketTypeAdapter());
            JsonObject jsonObject = gson.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        } else {
            if (arg1.equalsIgnoreCase("loginsuccess")) {
//                ChatGUI.main(args);

                ChatLogin.window.frmChat.dispose();
            } else if (arg1.equalsIgnoreCase("wrongpassword")) {
                ChatLogin.label_3.setText("Invalid password, try again!");
            } else if (arg1.equalsIgnoreCase("loggedinalready"))
                ChatLogin.label_3.setText("This account is already logged in!");
            else {
                if (arg1.startsWith("list:")) {
//                    ChatGUI.list.add(arg1.replaceAll("list:", ""));
                } else if (arg1.startsWith("listremove:")) {
//                    ChatGUI.list.remove(arg1.replaceAll("listremove:", ""));
                } else {
//                    ChatGUI.textArea.append(arg1 + "\n");
                }
            }
        }
    }
}