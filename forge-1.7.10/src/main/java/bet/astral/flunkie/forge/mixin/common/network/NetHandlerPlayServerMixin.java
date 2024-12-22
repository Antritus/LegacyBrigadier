package bet.astral.flunkie.forge.mixin.common.network;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.command.ExecuteResult;
import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.command.sender.BlockCommandSender;
import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.command.sender.EntityCommandSender;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.forge.server.Server;
import bet.astral.flunkie.world.Location;
import bet.astral.flunkie.world.entity.Player;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {
    @Shadow @Final private MinecraftServer serverController;
    @Shadow public EntityPlayerMP playerEntity;

    @Shadow public abstract void sendPacket(Packet p_147359_1_);

    @Overwrite
    private void handleSlashCommand(String p_147361_1_) {
        this.serverController.getCommandManager().executeCommand(this.playerEntity, p_147361_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void processTabComplete(C14PacketTabComplete p_147341_1_) {
        ArrayList<String> arraylist = Lists.newArrayList();

        for (String string : (Iterable<String>) this.serverController.getPossibleCompletions(this.playerEntity, p_147341_1_.func_149419_c())) {
            arraylist.add(string);
        }

        Suggestions suggestions = ((Server)MinecraftServer.getServer()).convertToSuggestions(p_147341_1_.func_149419_c(), arraylist);
        SuggestionResult result = new SuggestionResult(suggestions);
        LegacyBrigadier.getInstance()
                        .getPacketHandlerServer()
                                .sendTabComplete((Player) playerEntity, result);
    }
}
