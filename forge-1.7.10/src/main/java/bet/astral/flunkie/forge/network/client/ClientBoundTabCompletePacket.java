package bet.astral.flunkie.forge.network.client;

import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.forge.network.BetterPacket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientBoundTabCompletePacket implements BetterPacket,
        bet.astral.flunkie.network.client.ClientBoundTabCompletePacket {
    private SuggestionResult result;

    public ClientBoundTabCompletePacket(SuggestionResult result) {
        this.result = result;
    }

    public ClientBoundTabCompletePacket() {
    }

    @Override
    public SuggestionResult getSuggestionResult() {
        return result;
    }

    @Override
    public void setSuggestionResult(SuggestionResult suggestionResult) {
        this.result = suggestionResult;
    }

    @Override
    public void serialize(JsonObject object) {
        bet.astral.flunkie.network.client.ClientBoundTabCompletePacket.super.serialize(object);
    }

    @Override
    public void deserialize(JsonObject object) {
        bet.astral.flunkie.network.client.ClientBoundTabCompletePacket.super.deserialize(object);
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    public static class PacketHandler implements IMessageHandler<ClientBoundTabCompletePacket, IMessage> {
        @Override
        public IMessage onMessage(ClientBoundTabCompletePacket message, MessageContext ctx) {
            LegacyBrigadierV1_7_10.getInstance().getPacketHandlerClient().onTabCompleteReceive(message);
            return null;
        }

    }
}
