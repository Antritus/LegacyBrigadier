package bet.astral.brigadier.tests.text;

import bet.astral.brigadier.text.TranslationMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TranslationMessageImpl implements TranslationMessage {
    private final String translation;
    private final List<Object> placeholders;

    public TranslationMessageImpl(String translation, Object... placeholders) {
        this.translation = translation;
        this.placeholders = Arrays.asList(placeholders);
    }

    public TranslationMessageImpl(String translation, List<Object> placeholders) {
        this.translation = translation;
        this.placeholders = placeholders;
    }

    @Override
    public String getTranslationKey() {
        return translation;
    }

    @Override
    public List<Object> getTranslationPlaceholders() {
        return placeholders != null ? placeholders : Collections.emptyList();
    }

    @Override
    public String getString() {
        return translation;
    }
}
