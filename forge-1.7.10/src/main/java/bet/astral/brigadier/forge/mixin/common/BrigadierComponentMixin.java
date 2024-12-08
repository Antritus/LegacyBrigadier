package bet.astral.brigadier.forge.mixin.common;

import com.mojang.brigadier.Message;
import net.minecraft.util.ChatComponentStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatComponentStyle.class)
public abstract class BrigadierComponentMixin implements Message {
    @Shadow public abstract String getFormattedText();

    @Shadow public abstract String getUnformattedText();

    @Override
    public String getString() {
        return getUnformattedText();
    }


}
