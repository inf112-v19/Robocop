package inf112.skeleton.app.Socket;


import com.badlogic.gdx.Gdx;
import com.google.gson.JsonObject;
import inf112.skeleton.app.GUI.ChatBox;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.status.LoginResponseStatus;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameSocketHandler extends SimpleChannelInboundHandler<String> {
    private RoboRally game;

    public GameSocketHandler(RoboRally game) {
        this.game = game;
    }


    /**
     * Read message from server and decide what to do with it
     *
     * @param jsonObject
     */
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
                break;
            case TIME_TO_SELECT:
                TimeToSelectCardsPacket ttsc = TimeToSelectCardsPacket.parseJSON(jsonObject);
                if (RoboRally.gameBoard.hud != null) {
                    RoboRally.gameBoard.hud.roundSelectTime = ttsc.timeToSelect;
                }
                //TODO no idea when the hud is actually initialized, figure out so that feature #82 works.
                break;
            case CHATMESSAGE:
                if (ChatBox.chatBox != null) {
                    ChatMessagePacket chatMessagePacket = ChatMessagePacket.parseJSON(jsonObject);
                    ChatBox.chatBox.addMessage(chatMessagePacket);
                }
                break;
            case PLAYER_UPDATE:
                UpdatePlayerPacket playerUpdate = UpdatePlayerPacket.parseJSON(jsonObject);
                Player toUpdate = RoboRally.gameBoard.getPlayer(playerUpdate.getUUID());
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
            case SEND_FLAGS:
                FlagsPacket flags = FlagsPacket.parseJSON(jsonObject);
                RoboRally.gameBoard.receiveFlags(flags);
                break;
            case SEND_FLAG_UPDATE:
                FlagUpdatePacket flagUpdatePacket = FlagUpdatePacket.parseJSON(jsonObject);
                RoboRally.gameBoard.updateFlag(flagUpdatePacket);
                break;
            case REMOVE_PLAYER:
                PlayerRemovePacket playerRemovePacket = PlayerRemovePacket.parseJSON(jsonObject);
                RoboRally.gameBoard.removePlayer(playerRemovePacket);
                break;
            case JOIN_LOBBY_RESPONSE:
                LobbyJoinResponsePacket lobbyJoinResponsePacket = LobbyJoinResponsePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyJoin.add(lobbyJoinResponsePacket);
                }
                break;
            case LIST_LOBBIES:
                LobbiesListPacket lobbiesListPacket = LobbiesListPacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyList.add(lobbiesListPacket);
                }
                break;
            case STATE_CHANGED:
                StateChangePacket stateChangePacket = StateChangePacket.parseJSON(jsonObject);

                switch (stateChangePacket.getState()) {
                    case PLAYER_KICKED:
                        if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).leaveLobby();
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).setFreeze(false);
                        }
                        if (RoboRally.roboRally.gsm.peek() instanceof State_Playing) {
                            ((State_Playing) RoboRally.roboRally.gsm.peek()).stateChange = stateChangePacket.getState();

                        }
                        break;
                    case GAME_START:
                        if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                            ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_GameStart.add(Boolean.TRUE);
                        }
                        break;
                    case FORCE_CARDS:
                        RoboRally.gameBoard.hud.getPlayerDeck().forceUpdateSelected();

                        RoboRally.gameBoard.forceSelect();
//                        if (RoboRally.gameBoard.hud.hasDeck()) {
//                            RoboRally.gameBoard.hud.getPlayerDeck().removeDeck();
//                        }
                        break;
                }
                break;
            case LOBBY_UPDATE:
                LobbyUpdatePacket lobbyUpdatePacket = LobbyUpdatePacket.parseJSON(jsonObject);

                if (RoboRally.roboRally.gsm.peek() instanceof State_MainMenu) {
                    ((State_MainMenu) RoboRally.roboRally.gsm.peek()).packets_LobbyUpdates.add(lobbyUpdatePacket);
                }
                break;
            case ERROR_LOBBY_RESPONSE:
                RoboRally.roboRally.gsm.peek().addMessageToScreen("Lobby already exists...");
                break;
            default:
                System.err.println("Unhandled packet: " + packetId.name());
                System.out.println("data: " + jsonObject.get("data"));
                break;
        }
    }

    /**
     * Read and incoming packet, check if it follows the correct specifications, parse and send it to the packet handler.
     *
     * @param arg0
     * @param arg1
     */
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1) {
        System.out.println(arg1);
        if (arg1.startsWith("{")) {
            JsonObject jsonObject = Tools.GSON.fromJson(arg1, JsonObject.class);
            handleIncomingPacket(jsonObject);
        }
    }
}