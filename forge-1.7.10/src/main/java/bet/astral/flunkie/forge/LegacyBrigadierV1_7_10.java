package bet.astral.flunkie.forge;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.command.CommandSourceStack;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.forge.network.ClientPacketHandler;
import bet.astral.flunkie.forge.network.ServerPacketHandler;
import bet.astral.flunkie.forge.network.client.ClientBoundTabCompletePacket;
import bet.astral.flunkie.forge.network.server.ServerBoundTabCompletePacket;
import bet.astral.flunkie.network.PacketHandler;
import bet.astral.flunkie.registry.INamespaceRegistry;
import bet.astral.flunkie.text.JsonMessage;
import bet.astral.flunkie.text.TranslationMessage;
import bet.astral.flunkie.world.Block;
import bet.astral.flunkie.world.World;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
    private PacketHandler.Client clientNetwork= null;
    private PacketHandler.Server serverNetwork = null;

    public static Suggestion[] latestAutoComplete;
    public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Client
        network.registerMessage(ClientBoundTabCompletePacket.PacketHandler.class, bet.astral.flunkie.forge.network.client.ClientBoundTabCompletePacket.class, 0, Side.CLIENT);

        // Server
        network.registerMessage(ServerBoundTabCompletePacket.PacketHandler.class, ServerBoundTabCompletePacket.class, 1, Side.SERVER);

        clientNetwork = new ClientPacketHandler();
        serverNetwork = new ServerPacketHandler();
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
    public Gson getGson() {
        return gson;
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
    private final Gson gson = new Gson();
    @Override
    public JsonElement toJsonObj(Message message) {
        if (message instanceof LiteralMessage) {
            return gson.toJsonTree(message.getString());
        }
        return parser.parse(
                IChatComponent.Serializer.func_150696_a(
                        convertMessageToComponent(
                                message
                        )
                )
        );
    }

    @Override
    public PacketHandler.Server getPacketHandlerServer() {
        return serverNetwork;
    }

    @Override
    public PacketHandler.Client getPacketHandlerClient() {
        return clientNetwork;
    }

    @Override
    public INamespaceRegistry<Block> getBlockRegistry() {
        //noinspection unchecked
        return (INamespaceRegistry<Block>) net.minecraft.block.Block.blockRegistry;
    }
}
