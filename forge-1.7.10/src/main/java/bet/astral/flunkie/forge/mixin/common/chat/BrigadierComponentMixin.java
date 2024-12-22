package bet.astral.flunkie.forge.mixin.common.chat;

import com.mojang.brigadier.Message;
import net.minecraft.util.ChatComponentStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatComponentStyle.class)
public abstract class BrigadierComponentMixin implements Message {
    @Shadow public abstract String getFormattedText();

    @Override
    public String getString() {
        return getFormattedText();
    }


}
