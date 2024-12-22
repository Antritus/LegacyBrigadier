package bet.astral.flunkie.forge.mixin.common.chat;

import bet.astral.flunkie.text.TranslationMessage;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mixin(ChatComponentTranslation.class)
public abstract class TranslationComponentMixin implements TranslationMessage {
    @Shadow public abstract String getKey();

    @Shadow public abstract String getUnformattedTextForChat();

    @Shadow public abstract Object[] getFormatArgs();

    @Override
    public String getTranslationKey() {
        return getKey();
    }

    @Override
    public List<Object> getTranslationPlaceholders() {
        if (getFormatArgs() == null || getFormatArgs().length==0){
            return Collections.emptyList();
        }
        return Arrays.asList(getFormatArgs());
    }

    @Override
    public String getString() {
        IChatComponent com = (IChatComponent) this;
        return com.getFormattedText();
    }


}
