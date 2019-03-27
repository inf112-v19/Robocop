package inf112.skeleton.app.Socket;


import com.badlogic.gdx.Gdx;
import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.packet.data.*;
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
        Gdx.app.log("GameSocketHandler clientside -  handleIncomingPacket", "Handling incoming packet...");
        FromServer packetId = FromServer.values()[jsonObject.get("id").getAsInt()];
        switch (packetId) {
            case LOGINRESPONSE:
                GameState currentGameState = game.gsm.peek();
                if (currentGameState instanceof State_Login) {
                    // Read login-packet response and update the loginStatus variable in main menu.
                    ((State_Login) currentGameState).loginStatus = LoginResponseStatus.values()[
                            LoginResponsePacket.parseJSON(jsonObject).getStatusCode()];
                }
                break;
            case INIT_CLIENT:
//                RoboRally.gameBoard.setupPlayer(PlayerInitPacket.parseJSON(jsonObject));
                RoboRally.setClientInfo(ClientInitPacket.parseJSON(jsonObject));
                break;
            case INIT_MAP:
                RoboRally.roboRally.setBoard(InitMapPacket.parseJSON(jsonObject));
                break;
            case INIT_PLAYER:
                RoboRally.gameBoard.addPlayer(PlayerInitPacket.parseJSON(jsonObject));
                break;
            case INIT_LOCALPLAYER:
                RoboRally.gameBoard.setupPlayer(PlayerInitPacket.parseJSON(jsonObject));
//                RoboRally.setClientInfo(ClientInitPacket.parseJSON(jsonObject));
                break;
            case CHATMESSAGE:
                if (ScrollableTextbox.textbox != null) {
                    ChatMessagePacket chatMessagePacket = ChatMessagePacket.parseJSON(jsonObject);
                    ScrollableTextbox.textbox.push(chatMessagePacket);
                }
                break;

            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = UpdatePlayerPacket.parseJSON(jsonObject);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getName());
                toUpdate.updateRobot(playerUpdate);
                break;

            case CARD_PACKET:
                CardPacket packet = CardPacket.parseJSON(jsonObject);
                RoboRally.gameBoard.receiveCard(packet);
                break;
            case CARD_HAND_PACKET:
                CardHandPacket cardHandPacket = CardHandPacket.parseJSON(jsonObject);
                RoboRally.gameBoard.receiveCardHand(cardHandPacket);
                break;
            case REMOVE_PLAYER:
                PlayerRemovePacket playerRemovePacket = PlayerRemovePacket.parseJSON(jsonObject);
                RoboRally.gameBoard.removePlayer(playerRemovePacket);
                break;
            case JOIN_LOBBY_RESPONSE:
                LobbyJoinResponsePacket lobbyJoinResponsePacket = LobbyJoinResponsePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu)RoboRally.roboRally.gsm.peek()).packets_LobbyJoin.add(lobbyJoinResponsePacket);
                }
                break;
            case LIST_LOBBIES:
                LobbiesListPacket lobbiesListPacket = LobbiesListPacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu)RoboRally.roboRally.gsm.peek()).packets_LobbyList.add(lobbiesListPacket);
                }
                break;
            case STATE_CHANGED:
                StateChangePacket stateChangePacket = StateChangePacket.parseJSON(jsonObject);

                switch(stateChangePacket.getState()) {
                    case PLAYER_KICKED:
                        if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                            ((State_MainMenu)RoboRally.roboRally.gsm.peek()).leaveLobby();
                            ((State_MainMenu)RoboRally.roboRally.gsm.peek()).setFreeze(false);
                        }
                        break;

                }

                break;
            case LOBBY_UPDATE:
                LobbyUpdatePacket lobbyUpdatePacket = LobbyUpdatePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu)RoboRally.roboRally.gsm.peek()).packets_LobbyUpdates.add(lobbyUpdatePacket);
                }

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