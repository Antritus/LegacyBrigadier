package bet.astral.brigadier.forge.mixin.common;

import bet.astral.brigadier.text.JsonMessage;
import com.mojang.brigadier.Message;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatComponentText.class)
public class TextComponentMessageMixin implements JsonMessage {
    @Override
    public String getString() {
        return ((Message) this).getString();
    }
}
