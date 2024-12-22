package bet.astral.flunkie.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionResult {
    @Nullable
    private Suggestions suggestions;
    @Nullable
    private Message error;
    @Nullable
    private List<Message> usages;
    public SuggestionResult(@NotNull SuggestionResult modern, @NotNull SuggestionResult legacy){
        Message error = modern.getError();
        List<Message> usages = modern.getUsages();

        Map<String, Suggestion> suggestionMap = new HashMap<>();
        if (modern.getSuggestions() != null){
            for (Suggestion suggestion : modern.getSuggestions().getList()){
                suggestionMap.put(suggestion.getText().toLowerCase(), suggestion);
            }
        }
        if (legacy.getSuggestions() != null){
            for (Suggestion suggestion : legacy.getSuggestions().getList()){
                suggestionMap.putIfAbsent(suggestion.getText().toLowerCase(), suggestion);
            }
        }

        this.error = error;
        this.usages = usages;
        this.suggestions = new Suggestions(modern.getSuggestions().getRange(), new ArrayList<>(suggestionMap.values()));
    }
    public SuggestionResult(@NotNull Message error) {
        this.suggestions = null;
        this.usages = null;
        this.error = error;
    }

    public SuggestionResult(@NotNull Suggestions suggestions) {
        this.suggestions = suggestions;
        this.usages = null;
        this.error = null;
    }
    public SuggestionResult(@NotNull List<Message> usages) {
        this.suggestions = null;
        this.usages = usages;
        this.error = null;
    }

    public @Nullable Suggestions getSuggestions() {
        return suggestions;
    }

    public @Nullable Message getError() {
        return error;
    }

    public @Nullable List<Message> getUsages() {
        return usages;
    }
}
