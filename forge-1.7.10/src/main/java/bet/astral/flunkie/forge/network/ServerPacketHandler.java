package bet.astral.flunkie.forge.network;

import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.network.LegacyPacket;
import bet.astral.flunkie.network.Packet;
import bet.astral.flunkie.network.PacketHandler;
import bet.astral.flunkie.network.client.ClientBoundLegacyTabCompletePacket;
import bet.astral.flunkie.network.client.ClientBoundTabCompletePacket;
import bet.astral.flunkie.world.entity.Player;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S3APacketTabComplete;

import static bet.astral.flunkie.forge.LegacyBrigadierV1_7_10.network;

public class ServerPacketHandler extends PacketHandler.Server {
    @Override
    public ClientBoundTabCompletePacket createTabComplete(SuggestionResult result) {
        return new bet.astral.flunkie.forge.network.client.ClientBoundTabCompletePacket(result);
    }

    @Override
    public ClientBoundLegacyTabCompletePacket createLegacyTabComplete(String[] args) {

        return (ClientBoundLegacyTabCompletePacket) new S3APacketTabComplete(args);
    }

    @Override
    public void sendPacket(Player player, Packet packet) {
        if (packet instanceof LegacyPacket){
            net.minecraft.network.Packet mojangPacket = (net.minecraft.network.Packet) packet;
            EntityPlayerMP mp = (EntityPlayerMP) player;
            mp.playerNetServerHandler.sendPacket(mojangPacket);
        } else {
            IMessage forgePacket = (IMessage) packet;
            network.sendTo(forgePacket, (EntityPlayerMP) player);
        }
    }
}
