package bet.astral.flunkie.forge.mixin.common.network.server;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.network.server.ServerBoundLegacyTabCompletePacket;
import bet.astral.flunkie.world.entity.Player;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(C14PacketTabComplete.class)
public abstract class ServerBoundTabCompletePacketMixin implements ServerBoundLegacyTabCompletePacket {

    @Shadow
    private String field_149420_a;

    @Shadow
    public abstract String func_149419_c();

    @Shadow
    public abstract void processPacket(INetHandlerPlayServer p_148833_1_);

    @Shadow
    public abstract String serialize();

    @Override
    public String getCommand() {
        return func_149419_c();
    }

    @Override
    public int getCursor() {
        return 0;
    }

    @Override
    public void setCommand(String command) {
        field_149420_a = command;
    }

    @Override
    public void setCursor(int cursor) {
    }

    @Overwrite
    public void processPacket(INetHandler p_148833_1_) {
        NetHandlerPlayServer handler = (NetHandlerPlayServer) p_148833_1_;
        Player player = (Player) handler.playerEntity;
        ((BaseCommandManager) MinecraftServer.getServer().getCommandManager())
                .getBrigadierManager().suggest(player,
                        getCommand(),
                        getCursor()
                ).thenAccept(s -> {
                    LegacyBrigadier
                            .getInstance()
                            .getPacketHandlerServer()
                            .sendTabComplete(player, s);
                });
    }
}
