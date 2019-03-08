package inf112.skeleton.app.Socket;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.gameStates.Playing.HUD;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameSocketHandler extends SimpleChannelInboundHandler<String> {

    private Gson gson = new Gson();
    private RoboRally game;

    public GameSocketHandler(RoboRally game) {
        this.game = game;
        game.setSocketHandler(this);
    }


    public void handleIncomingPacket(JsonObject jsonObject) {
        System.out.println("Handling incoming packet...");
        OutgoingPacket packetId = OutgoingPacket.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                GameState currentGameState = game.gsm.peek();
                if (currentGameState instanceof State_MainMenu) {
                    // Read login-packet response and update the loginStatus variable in main menu.
                    ((State_MainMenu) currentGameState).loginStatus = LoginResponseStatus.values()[
                            gson.fromJson(jsonObject.get("data"), LoginResponsePacket.class).getStatusCode()];
                }
                break;

            case INIT_PLAYER:
                RoboRally.gameBoard.addPlayer(gson.fromJson(jsonObject.get("data"), PlayerInitPacket.class));
                break;

            case CHATMESSAGE:
                if (ScrollableTextbox.textbox != null) {
                    ChatMessagePacket chatMessagePacket = gson.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
                    ScrollableTextbox.textbox.push(chatMessagePacket);
                }
                break;

            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = gson.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getName());
                toUpdate.updateRobot(playerUpdate);
                break;

            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
                break;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) {
        System.out.println(arg1);
        if (arg1.startsWith("{")) {
            JsonObject jsonObject = gson.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        }
    }
}