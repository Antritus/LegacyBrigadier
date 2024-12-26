package bet.astral.flunkie.forge.network;

import bet.astral.flunkie.network.ModernPacket;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public interface BetterPacket
        extends IMessage, ModernPacket
{
    Logger log = LogManager.getLogger(BetterPacket.class);

    @Override
    default void fromBytes(ByteBuf buf) {
        try {
            PacketBuffer buffer = new PacketBuffer(buf);
            String json = buffer.readStringFromBuffer(32767);
            JsonObject obj = getGson().fromJson(json, JsonObject.class);
            deserialize(obj);
        } catch (IOException e){
            log.error("e: ", e);
        }
    }

    @Override
    default void toBytes(ByteBuf buf) {
        try {
            PacketBuffer buffer = new PacketBuffer(buf);
            JsonObject obj = new JsonObject();
            serialize(obj);

            String string = getGson()
                    .toJson(obj);
            buffer.writeStringToBuffer(string);
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
    }
}
