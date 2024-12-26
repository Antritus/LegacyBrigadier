package bet.astral.flunkie.forge.network.server;

import bet.astral.flunkie.command.CommandManager;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.forge.network.BetterPacket;
import bet.astral.flunkie.forge.network.client.ClientBoundTabCompletePacket;
import bet.astral.flunkie.world.entity.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;


public class ServerBoundTabCompletePacket implements BetterPacket,
        bet.astral.flunkie.network.server.ServerBoundTabCompletePacket {
    private String command;
    private int cursor;

    public ServerBoundTabCompletePacket(String command, int cursor) {
        this.command = command;
        this.cursor = cursor;
    }

    public ServerBoundTabCompletePacket() {
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public int getCursor() {
        return cursor;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    @Override
    public void serialize(@NotNull JsonObject object) {
        bet.astral.flunkie.network.server.ServerBoundTabCompletePacket.super.serialize(object);
    }

    @Override
    public void deserialize(@NotNull JsonObject object) {
        bet.astral.flunkie.network.server.ServerBoundTabCompletePacket.super.deserialize(object);
    }

    @Override
    public Gson getGson() {
        return gson;
    }
    public static class PacketHandler implements IMessageHandler<ServerBoundTabCompletePacket, IMessage> {
        @Override
        public IMessage onMessage(ServerBoundTabCompletePacket message, MessageContext ctx) {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            BaseCommandManager commandManager = (BaseCommandManager) minecraftServer.getCommandManager();
            CommandManager brigadier = commandManager.getBrigadierManager();

            Player player = (Player) ctx.getServerHandler().playerEntity;

            brigadier.suggest(
                    player,
                    message.getCommand(),
                    message.getCursor()
            ).thenAccept(s->{
                LegacyBrigadierV1_7_10.getInstance()
                                .getPacketHandlerServer()
                                        .sendPacket(player,
                                                new ClientBoundTabCompletePacket(s)
                                                );
            });
            return null;
        }
    }

}
