package bet.astral.brigadier.forge;

import bet.astral.brigadier.LegacyBrigadier;
import bet.astral.brigadier.command.CommandSourceStack;
import bet.astral.brigadier.forge.commands.BaseCommandManager;
import bet.astral.brigadier.forge.network.client.ServerBoundTabCompletePacket;
import bet.astral.brigadier.forge.network.server.ClientBoundTabCompletePacket;
import bet.astral.brigadier.text.JsonMessage;
import bet.astral.brigadier.text.TranslationMessage;
import bet.astral.brigadier.world.World;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.lang.reflect.Field;
import java.util.List;

@Mod(modid = LegacyBrigadierV1_7_10.MODID, version = LegacyBrigadierV1_7_10.VERSION)
public class LegacyBrigadierV1_7_10 extends LegacyBrigadier {
    public static final String MODID = "legacybrigadier";
    public static final String VERSION = "1.0.0";

    public static Suggestion[] latestAutoComplete;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        network.registerMessage(ClientBoundTabCompletePacket.PacketHandler.class, ClientBoundTabCompletePacket.class, 0, Side.SERVER);
        network.registerMessage(ServerBoundTabCompletePacket.PacketHandler.class, ServerBoundTabCompletePacket.class, 0, Side.CLIENT);

    }

    public LegacyBrigadierV1_7_10(){
        LegacyBrigadier impl = getInstance();
        if (impl != null) {
            return;
        }
        Class<?> clazz = LegacyBrigadier.class;
        try {
            Field field = clazz.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static IChatComponent convertMessageToComponent(Message message){
        if (message instanceof LiteralMessage){
            return (IChatComponent) legacyMessage(message.getString());
        } else if (message instanceof TranslationMessage) {
            TranslationMessage translationMessage = (TranslationMessage) message;
            if (translationMessage.getTranslationPlaceholders()==null||translationMessage.getTranslationPlaceholders().isEmpty()){
                return (IChatComponent) translationMessage(translationMessage.getTranslationKey());
            }
            return (IChatComponent) translationMessage(translationMessage.getTranslationKey(), translationMessage.getTranslationPlaceholders());
        } else if (message instanceof JsonMessage){
            JsonMessage jsonMessage = (JsonMessage) message;
            return (IChatComponent) jsonMessage(jsonMessage.getString());
        }
        return (IChatComponent) legacyMessage(message.getString());
    }

    @Override
    public World getDefaultWorld() {
        return (World) MinecraftServer.getServer().worldServers[0];
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
        ((BaseCommandManager) MinecraftServer.getServer().getCommandManager()).getBrigadierManager()
                .register(command);
    }

    @Override
    public TranslationMessage createTranslationMessage(String message) {
        return (TranslationMessage) new ChatComponentTranslation(message);
    }

    @Override
    public TranslationMessage createTranslationMessage(String message, List<Object> placeholders) {
        return (TranslationMessage) new ChatComponentTranslation(message, placeholders.toArray());
    }

    @Override
    public TranslationMessage createTranslationMessage(String message, Object... placeholders) {
        return (TranslationMessage) new ChatComponentTranslation(message, placeholders);
    }

    @Override
    public Message createLegacyMessage(String message) {
        return (Message) new ChatComponentText(message);
    }

    @Override
    public JsonMessage createJsonMessage(String message) {
        return (JsonMessage) new ChatComponentText(message);
    }

    private final JsonParser parser = new JsonParser();
    @Override
    public JsonObject toJsonObj(Message message) {
        JsonObject object = parser.parse(
                IChatComponent.Serializer.func_150696_a(
                        convertMessageToComponent(
                                message
                        )
                )
        ).getAsJsonObject();
        return null;
    }
}
