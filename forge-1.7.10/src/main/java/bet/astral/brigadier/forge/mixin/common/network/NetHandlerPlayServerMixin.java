package bet.astral.brigadier.forge.mixin.common.network;

import bet.astral.brigadier.command.CommandManager;
import bet.astral.brigadier.forge.commands.BaseCommandManager;
import bet.astral.brigadier.forge.server.Server;
import bet.astral.brigadier.world.entity.Player;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
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

    public boolean processTabCompleteBrig(C14PacketTabComplete packetTabComplete){
        String command = packetTabComplete.func_149419_c();
        // Legacy Brig
        BaseCommandManager commandManager = (BaseCommandManager) MinecraftServer.getServer().getCommandManager();
        CommandManager brigadierManager = commandManager.getBrigadierManager();
        Player brigPlayer = (Player) playerEntity;
        Suggestions brigSuggestions = brigadierManager.suggest(brigPlayer, command, 0);

        if (brigSuggestions != null){
            sendTabComplete(brigSuggestions);
            return true;
        }
        return false;
    }

    public void sendTabComplete(Suggestions brigSuggestions){
        if (brigSuggestions == null){
            return;
        }
        // Create packet and send it to the client
        Suggestion[] suggestions = brigSuggestions.getList().toArray(new Suggestion[0]);
        S3APacketTabComplete packet = new S3APacketTabComplete();
        ClientBoundTabCompletePacket packetLegacyBrig = (ClientBoundTabCompletePacket) packet;
        packetLegacyBrig.setSuggestions(suggestions);

        this.playerEntity.playerNetServerHandler.sendPacket(packet);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void processTabComplete(C14PacketTabComplete p_147341_1_) {
        if (processTabCompleteBrig(p_147341_1_)){
            return;
        }

        ArrayList<String> arraylist = Lists.newArrayList();

        for (String string : (Iterable<String>) this.serverController.getPossibleCompletions(this.playerEntity, p_147341_1_.func_149419_c())) {
            arraylist.add(string);
        }

        Suggestions suggestions = ((Server)MinecraftServer.getServer()).convertToSuggestions(p_147341_1_.func_149419_c(), arraylist);
        sendTabComplete(suggestions);
    }
}
