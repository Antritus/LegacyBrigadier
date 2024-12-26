package bet.astral.flunkie;

import bet.astral.flunkie.command.CommandSourceStack;
import bet.astral.flunkie.network.PacketHandler;
import bet.astral.flunkie.registry.INamespaceRegistry;
import bet.astral.flunkie.text.JsonMessage;
import bet.astral.flunkie.text.TranslationMessage;
import bet.astral.flunkie.world.Block;
import bet.astral.flunkie.world.World;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.List;

public abstract class LegacyBrigadier {
    private static LegacyBrigadier instance;
    public abstract Gson getGson();

    public static LegacyBrigadier getInstance() {
        return instance;
    }

    public abstract World getDefaultWorld();

    public static INamespaceRegistry<Block> registryBlock() {
        return getInstance().getBlockRegistry();
    }

    public static TranslationMessage translationMessage(String message) {
        return getInstance().createTranslationMessage(message);
    }
    public static TranslationMessage translationMessage(String message, List<Object> placeholders) {
        return getInstance().createTranslationMessage(message, placeholders);
    }
    public static TranslationMessage translationMessage(String message, Object... placeholders) {
        return getInstance().createTranslationMessage(message, placeholders);
    }
    public static Message legacyMessage(String message) {
        return getInstance().createLegacyMessage(message);
    }
    public static JsonMessage jsonMessage(String message) {
        return getInstance().createJsonMessage(message);
    }
    public static JsonElement messageToJson(Message message){
        return getInstance().toJsonObj(message);
    }
    public static void command(LiteralArgumentBuilder<CommandSourceStack> command){
        getInstance().registerCommand(command);
    }
    public abstract void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command);
    public abstract TranslationMessage createTranslationMessage(String message);
    public abstract TranslationMessage createTranslationMessage(String message, List<Object> placeholders);
    public abstract TranslationMessage createTranslationMessage(String message, Object... placeholders);
    public abstract Message createLegacyMessage(String message);
    public abstract JsonMessage createJsonMessage(String message);
    public abstract JsonElement toJsonObj(Message message);
    public abstract PacketHandler.Server getPacketHandlerServer();
    public abstract PacketHandler.Client getPacketHandlerClient();

    public abstract INamespaceRegistry<Block> getBlockRegistry();
}
