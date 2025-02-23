package bet.astral.flunkie.network.server;

import bet.astral.flunkie.network.LegacyPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface ServerBoundLegacyTabCompletePacket extends LegacyPacket {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    String getCommand();
    int getCursor();
    void setCommand(String command);
    void setCursor(int cursor);

    default void serialize(@NotNull JsonObject obj) {
        obj.addProperty("command", getCommand());
        obj.addProperty("cursor", getCursor());
    }
    default void deserialize(@NotNull JsonObject obj) {
        this.setCommand(obj.get("command").getAsString());
        this.setCursor(obj.get("cursor").getAsInt());
    }

    default Gson getGson(){
        return gson;
    }
}
