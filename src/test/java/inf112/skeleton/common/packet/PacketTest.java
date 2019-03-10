package inf112.skeleton.common.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Card;
import static inf112.skeleton.common.specs.CardType.values;

import inf112.skeleton.common.utility.Tools;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Random;

public class PacketTest {
    Random random = new Random();
    static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Test
    public void cardPacketCreateGetTest() {
        for(int i = 0; i < 1000; i++) {
            Card card = new Card(random.nextInt(10000), values()[random.nextInt(values().length)]);
            CardPacket packet = new CardPacket(card);
            Assert.assertEquals(card.getPriority(), packet.getPriority());
            Assert.assertEquals(card.getType(), packet.getType());
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
