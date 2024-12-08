package bet.astral.brigadier.forge.client.network;

import net.minecraft.network.play.server.S3APacketTabComplete;

public interface ClientNetworkHandler {
    void handleBrigadierTabComplete(S3APacketTabComplete tabComplete);
}
