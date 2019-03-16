package inf112.skeleton.common.packet;

public enum FromServer {
    LOGINRESPONSE,
    INIT_PLAYER,
    INIT_ALLPLAYERS, //TODO: IMplement
    REMOVE_PLAYER,
    CHATMESSAGE,
    PLAYER_UPDATE,
    CARD_PACKET,
    CARD_HAND_PACKET,
}
