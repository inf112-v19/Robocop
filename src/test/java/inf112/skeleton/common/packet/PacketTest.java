package inf112.skeleton.common.packet;

import inf112.skeleton.common.packet.data.CardPacket;
import inf112.skeleton.common.packet.data.ChatMessagePacket;
import inf112.skeleton.common.specs.Card;

import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.card.CardDeck;
import org.junit.*;

import java.util.Random;

public class PacketTest {
    Random random = new Random();
    static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Test
    public void cardPacketCreateGetTest() {
        CardDeck deck = new CardDeck();
        for(int i = 0; i < 1000; i++) {
            if(deck.size() < 1) {       //I choose to do the test like this, instead of adding dealt cards back into
                deck = new CardDeck();  //the deck, as the deck will spend time shuffling each and every time that happens.
            }
            Card card = deck.dealCard();
            CardPacket packet = new CardPacket(card);
            Assert.assertEquals(card.getPriority(), packet.getPriority());
            Assert.assertEquals(card.getType(),
                    Tools.CARD_RECONSTRUCTOR.reconstructCard(packet.getPriority()).getType());
        }
    }

    @Test
    public void chatMessagePacketCreateGetTest() {
        for(int i = 0; i < 1000; i++) {
            String message = randomStringGenerator();
            ChatMessagePacket packet = new ChatMessagePacket(message);
            Assert.assertEquals(message, packet.getMessage());
        }
    }

    @Test
    public void packetTransmitReconstructTest() {
        for(int i = 0; i < 1000; i++) {
            String message = randomStringGenerator();
            ChatMessagePacket send = new ChatMessagePacket(message);
            String jsonString = Tools.GSON.toJson(send);
            ChatMessagePacket receive = Tools.GSON.fromJson(jsonString, ChatMessagePacket.class);

            Assert.assertEquals(message, send.getMessage());
            Assert.assertEquals(send.getMessage(), receive.getMessage());
        }
    }

    private String randomStringGenerator() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < random.nextInt(150); i++) {
            builder.append(ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return builder.toString();
    }
}
