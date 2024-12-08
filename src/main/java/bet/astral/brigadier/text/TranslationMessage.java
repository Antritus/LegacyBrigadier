package bet.astral.brigadier.text;

import com.mojang.brigadier.Message;

import java.util.List;

public interface TranslationMessage extends Message {
    String getTranslationKey();
    List<Object> getTranslationPlaceholders();
}
