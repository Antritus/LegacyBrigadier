package bet.astral.flunkie.forge.server;

import com.mojang.brigadier.suggestion.Suggestions;

import java.util.List;

public interface Server {
    Suggestions convertToSuggestions(String cmd, List<String> suggestions);
}