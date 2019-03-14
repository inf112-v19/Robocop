package inf112.skeleton.app.Socket;


import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameSocketHandler extends SimpleChannelInboundHandler<String> {
    private RoboRally game;

    public GameSocketHandler(RoboRally game) {
        this.game = game;
        game.setSocketHandler(this);
    }


    public void handleIncomingPacket(JsonObject jsonObject) {
        System.out.println("Handling incoming packet...");
        FromServer packetId = FromServer.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                GameState currentGameState = game.gsm.peek();
                if (currentGameState instanceof State_MainMenu) {
                    // Read login-packet response and update the loginStatus variable in main menu.
                    ((State_MainMenu) currentGameState).loginStatus = LoginResponseStatus.values()[
                            Tools.GSON.fromJson(jsonObject.get("data"), LoginResponsePacket.class).getStatusCode()];
                }
                break;

            case INIT_PLAYER:
                RoboRally.gameBoard.addPlayer(Tools.GSON.fromJson(jsonObject.get("data"), PlayerInitPacket.class));
                break;

            case CHATMESSAGE:
                if (ScrollableTextbox.textbox != null) {
                    ChatMessagePacket chatMessagePacket = Tools.GSON.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
                    ScrollableTextbox.textbox.push(chatMessagePacket);
                }
                break;

            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = Tools.GSON.fromJson(jsonObject.get("data"), UpdatePlayerPacket.class);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getName());
                toUpdate.updateRobot(playerUpdate);
                break;

            case CARD_PACKET:
                CardPacket packet = CardPacket.parseJSON(jsonObject);
                RoboRally.gameBoard.receiveCard(packet);
                break;
            case CARD_HAND_PACKET:
                CardHandPacket cardHandPacket = Tools.GSON.fromJson(jsonObject.get("data"), CardHandPacket.class);
                RoboRally.gameBoard.receiveCardHand(cardHandPacket);
                break;
            case REMOVE_PLAYER:
                PlayerRemovePacket playerRemovePacket = PlayerRemovePacket.parseJSON(jsonObject);
                RoboRally.gameBoard.removePlayer(playerRemovePacket);
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
            JsonObject jsonObject = Tools.GSON.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        }
    }
}