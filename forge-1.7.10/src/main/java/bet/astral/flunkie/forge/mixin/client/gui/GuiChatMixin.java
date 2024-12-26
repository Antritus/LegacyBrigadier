package bet.astral.flunkie.forge.mixin.client.gui;

import bet.astral.flunkie.forge.client.gui.ChatGUI;
import bet.astral.flunkie.forge.client.gui.SuggestionGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin implements ChatGUI {
    @Shadow
    protected GuiTextField inputField;
    private SuggestionGui suggestionGui;

    @Override
    public SuggestionGui getSuggestionGui() {
        return suggestionGui;
    }

    @Inject(method = "initGui()V",
            at=@At("TAIL")
    )
    public void init(CallbackInfo ci){
        GuiChat chat = (GuiChat) (Object) this;
        suggestionGui = new SuggestionGui(inputField);
    }

    @Inject(method = "onGuiClosed",
            at = @At("TAIL")
    )
    private void unFocus(CallbackInfo ci){
        suggestionGui.onDeFocus();
    }

    @Inject(
            method = "keyTyped(CI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiTextField;textboxKeyTyped(CI)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void textBoxTyped(char character, int key, CallbackInfo ci) {
        suggestionGui.onKeyTyped();
    }

    @Inject(method = "keyTyped(CI)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void onTabCompletion(char character, int key, CallbackInfo ci){
        if (suggestionGui != null && suggestionGui.hasSuggestions()) {
            if (key == Keyboard.KEY_TAB) {
                suggestionGui.onTabComplete();
                ci.cancel();
            } else if (key == Keyboard.KEY_DOWN) {
                suggestionGui.onArrowDown();
                ci.cancel();
            } else if (key == Keyboard.KEY_UP) {
                suggestionGui.onArrowUp();
                ci.cancel();
            }
        }
    }

    @Overwrite
    public void func_146403_a(String p_146403_1_) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().addToSentMessages(p_146403_1_);

        /*
        if (ClientCommandHandler.instance.executeCommand(mc.thePlayer, p_146403_1_) != 0){
            return;
        }

         */
        System.out.println("Hi");
        mc.thePlayer.sendChatMessage(p_146403_1_);
    }

    @Inject(
            method = "drawScreen(IIF)V",
            at = @At("TAIL")
    )
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_, CallbackInfo ci) {
        suggestionGui.renderSuggestions();
//        renderSuggestions();
    }
}