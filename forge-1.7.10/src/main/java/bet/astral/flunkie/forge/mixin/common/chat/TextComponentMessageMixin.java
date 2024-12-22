package bet.astral.flunkie.forge.mixin.common.chat;

import bet.astral.flunkie.text.JsonMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatComponentText.class)
public abstract class TextComponentMessageMixin implements JsonMessage {
    @Shadow public abstract String getUnformattedTextForChat();

    @Shadow public abstract String getChatComponentText_TextValue();

    @Override
    public String getString() {
        IChatComponent com = (IChatComponent) this;
        return com.getFormattedText();
    }
}
