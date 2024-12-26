package bet.astral.flunkie.command.arguments;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.command.Exceptions;
import bet.astral.flunkie.world.Block;
import bet.astral.flunkie.registry.Identifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BlockArgument implements ArgumentType<Block> {
    private static final Collection<String> examples = Arrays.asList(
            Identifier.minecraft("dirt").full(),
            Identifier.minecraft("stone").full(),
            Identifier.minecraft("cobblestone").full(),
            Identifier.minecraft("bedrock").full()
            );

    public static BlockArgument blockArg() {
        return new BlockArgument();
    }

    public static Block getBlock(final CommandContext<?> context, final String name){
        return context.getArgument(name, Block.class);
    }

    @Override
    public Block parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        String value = reader.getString();
        if (value.isEmpty()){
            return null;
        }
        Optional<Block> block = LegacyBrigadier.registryBlock().get(Identifier.of(value));
        if (!block.isPresent()){
            reader.setCursor(start);
            throw Exceptions.UNKNOWN_BLOCK.createWithContext(reader, value);
        }
        return block.orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(()-> new Suggestions(context.getRange(), LegacyBrigadier.registryBlock().getRegistered().stream()
                .map(value-> new Suggestion(context.getRange(), value.getIdentifier().toString(), value.getTranslation())).collect(Collectors.toList())));
    }

    @Override
    public Collection<String> getExamples() {
        return examples;
    }
}
