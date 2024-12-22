package bet.astral.flunkie.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface ModernPacket extends Packet{
    void serialize(JsonObject object);
    void deserialize(JsonObject object);
    Gson getGson();
}
