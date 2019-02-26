package inf112.skeleton.server.packet;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class PacketTypeAdapter extends TypeAdapter<PacketReciever> {

    @Override
    public void write(JsonWriter jsonWriter, PacketReciever packetReciever) throws IOException {
        if(packetReciever == null) {
            jsonWriter.nullValue();
            return;
        }
    }

    @Override
    public final PacketReciever read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        PacketReciever wrapper = new PacketReciever();

        // HERE IS WHERE YOU GET TO WRITE CODE
        System.out.println("Herrroww");

        return wrapper;
    }
}
