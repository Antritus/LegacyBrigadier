package bet.astral.flunkie.network;

import bet.astral.flunkie.command.SuggestionResult;
import bet.astral.flunkie.network.client.ClientBoundLegacyTabCompletePacket;
import bet.astral.flunkie.network.client.ClientBoundTabCompletePacket;
import bet.astral.flunkie.network.server.ServerBoundLegacyTabCompletePacket;
import bet.astral.flunkie.network.server.ServerBoundTabCompletePacket;
import bet.astral.flunkie.world.entity.Player;
import com.mojang.brigadier.suggestion.Suggestion;
import org.jetbrains.annotations.Nullable;

public abstract class PacketHandler {
    public abstract static class Client {
        public boolean isLegacyBrigadierSupport;
        public PacketHandler packetHandler;

        public void onConnect() {
            isLegacyBrigadierSupport = true;
        }
        public abstract void onTabCompleteReceive(ClientBoundTabCompletePacket packet);
        public abstract void onTabCompleteReceive(ClientBoundLegacyTabCompletePacket packet);

        public void sendTabCompleteRequest(String command, int cursor) {
            if (command.isEmpty()){
                cursor = 0;
            }
            if (!isLegacyBrigadierSupport){
                ServerBoundLegacyTabCompletePacket packet = createLegacyTabCompletionRequest(command, cursor);
                sendPacket(packet);
                return;
            }
            ServerBoundTabCompletePacket packet = createTabCompletionRequest(command, cursor);
            sendPacket(packet);
        }

        public abstract ServerBoundTabCompletePacket createTabCompletionRequest(String command, int cursor);
        public abstract ServerBoundLegacyTabCompletePacket createLegacyTabCompletionRequest(String command, int cursor);

        public abstract void sendPacket(Packet packet);
    }
    public abstract static class Server {
        public PacketHandler packetHandler;
        public void sendTabComplete(Player player, SuggestionResult result){
            sendPacket(player, createTabCompletePacket(player, result));
        }
        @Nullable
        public Packet createTabCompletePacket(Player player, SuggestionResult result){
            ClientBoundTabCompletePacket packet = createTabComplete(result);
            if (!player.isUsingLegacyBrigadier()){
                return convertToLegacyPacket(packet);
            } else {
                return packet;
            }
        }

        public ClientBoundLegacyTabCompletePacket convertToLegacyPacket(ClientBoundTabCompletePacket clientBoundTabCompletePacket){
            if (clientBoundTabCompletePacket.getSuggestionResult().getSuggestions() != null){
                return createLegacyTabComplete(clientBoundTabCompletePacket.getSuggestionResult().getSuggestions().getList().stream().map(Suggestion::getText).toArray(String[]::new));
            } else {
                return createLegacyTabComplete(new String[0]);
            }
        }
        public abstract ClientBoundTabCompletePacket createTabComplete(SuggestionResult result);
        public abstract ClientBoundLegacyTabCompletePacket createLegacyTabComplete(String[] args);
        public abstract void sendPacket(Player player, Packet packet);
    }
}
