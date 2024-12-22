package bet.astral.flunkie.forge.mixin.common.server;

import bet.astral.flunkie.forge.server.Server;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements Server {
    public Suggestions convertToSuggestions(String cmd, List<String> suggestions){
        String[] spaceSplit = cmd.split(" ");
        if (spaceSplit.length==0){
            return new Suggestions(StringRange.at(0), Collections.emptyList());
        }
        String split = spaceSplit[spaceSplit.length-1];
        int length = split.length();
        StringRange range = StringRange.at(length);
        if (suggestions.isEmpty()){
            return null;
        }
        List<Suggestion> suggestionList = suggestions.stream().map(s->new Suggestion(range, s)).collect(Collectors.toList());
        return Suggestions.create(split, suggestionList);
    }
}
