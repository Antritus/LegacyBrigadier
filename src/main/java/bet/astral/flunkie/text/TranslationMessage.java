package bet.astral.flunkie.text;

import com.mojang.brigadier.Message;

import java.util.List;

public interface TranslationMessage extends Message {
    String getTranslationKey();
    List<Object> getTranslationPlaceholders();
}
