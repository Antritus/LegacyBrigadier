package bet.astral.brigadier;

import bet.astral.brigadier.command.CommandSourceStack;
import bet.astral.brigadier.text.JsonMessage;
import bet.astral.brigadier.text.TranslationMessage;
import bet.astral.brigadier.world.World;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.List;

public abstract class LegacyBrigadier {
    private static LegacyBrigadier instance;
    public static LegacyBrigadier getInstance() {
        return instance;
    }

    public abstract World getDefaultWorld();

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
    public static JsonObject messageToJson(Message message){
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
    public abstract JsonObject toJsonObj(Message message);
}
