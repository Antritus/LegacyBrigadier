package bet.astral.flunkie.forge.network;

import bet.astral.flunkie.forge.client.gui.ChatGUI;
import bet.astral.flunkie.network.LegacyPacket;
import bet.astral.flunkie.network.Packet;
import bet.astral.flunkie.network.PacketHandler;
import bet.astral.flunkie.network.client.ClientBoundLegacyTabCompletePacket;
import bet.astral.flunkie.network.client.ClientBoundTabCompletePacket;
import bet.astral.flunkie.network.server.ServerBoundLegacyTabCompletePacket;
import bet.astral.flunkie.network.server.ServerBoundTabCompletePacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.C14PacketTabComplete;

import static bet.astral.flunkie.forge.LegacyBrigadierV1_7_10.network;

public class ClientPacketHandler extends PacketHandler.Client {
    @Override
    public void onTabCompleteReceive(ClientBoundTabCompletePacket packet) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChat){
            ChatGUI gui = (ChatGUI) screen;
            gui.getSuggestionGui()
                    .onSuggestionReceive(packet.getSuggestionResult());
        }
    }

    @Override
    public void onTabCompleteReceive(ClientBoundLegacyTabCompletePacket packet) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChat){
            ChatGUI gui = (ChatGUI) screen;
            gui.getSuggestionGui()
                    .onSuggestionReceive(packet.getSuggestions());
        }
    }

    @Override
    public ServerBoundTabCompletePacket createTabCompletionRequest(String command, int cursor) {
        return new bet.astral.flunkie.forge.network.server.ServerBoundTabCompletePacket(command, cursor);
    }

    @Override
    public ServerBoundLegacyTabCompletePacket createLegacyTabCompletionRequest(String command, int cursor) {
        return (ServerBoundLegacyTabCompletePacket) new C14PacketTabComplete(command);
    }

    @Override
    public void sendPacket(Packet packet) {
        if (packet instanceof LegacyPacket){
            net.minecraft.network.Packet mojangPacket = (net.minecraft.network.Packet) packet;
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(mojangPacket);
        } else if (packet instanceof IMessage) {
            IMessage forgePacket = (IMessage) packet;
            network.sendToServer(forgePacket);
        }
    }
}
