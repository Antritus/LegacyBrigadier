package bet.astral.flunkie.forge.mixin.client.network;

import bet.astral.flunkie.forge.client.gui.ChatGUI;
import bet.astral.flunkie.forge.client.gui.SuggestionGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3APacketTabComplete;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin   {
    @Shadow private Minecraft gameController;

    @Overwrite
    public void handleTabComplete(S3APacketTabComplete p_147274_1_) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiChat){
            GuiChat chat = (GuiChat) screen;
            ChatGUI chatGUI = (ChatGUI) chat;

            SuggestionGui gui = chatGUI.getSuggestionGui();
            gui.onSuggestionReceive(p_147274_1_.func_149630_c());
        }
    }
}
