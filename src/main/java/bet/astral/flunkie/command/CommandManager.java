package bet.astral.flunkie.command;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.command.sender.ConsoleCommandSender;
import bet.astral.flunkie.command.sender.EntityCommandSender;
import bet.astral.flunkie.world.Location;
import bet.astral.flunkie.world.entity.Entity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.net.www.MessageHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandManager {
    private final CommandDispatcher<CommandSourceStack> commandDispatcher = new CommandDispatcher<>();
    private final LegacyCommandManager legacyCommandManager;
    public CommandManager(LegacyCommandManager legacyCommandManager){
        this.legacyCommandManager = legacyCommandManager;
    }

    public <S extends CommandSourceStack> void register(LiteralArgumentBuilder<S> command){
        commandDispatcher.register((LiteralArgumentBuilder<CommandSourceStack>) command);
    }

    public Object dispatch(@NotNull CommandSender commandSender, @Nullable Location location, @NotNull String command) {
        try {
            return handleExecution(commandSender, location, command);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Object handleExecution(@NotNull CommandSender commandSender, @Nullable Location location, @NotNull String command) throws CommandSyntaxException {
        if (!commandSender.isValid()){
            return ExecuteResult.INVALID_PROFILE;
        }

        if (command.isEmpty()){
            boolean exists = legacyCommandManager.commandExists(commandSender, command);
            if (!exists){
                return ExecuteResult.UNKNOWN_COMMAND;
            } else {
                return legacyCommandManager.execute(commandSender, command);
            }
        }
        CommandSourceStack commandSourceStack = new CommandSourceStack(commandSender, location, commandSender instanceof Entity ? (Entity) commandSender : null);
        ParseResults<CommandSourceStack> parseResults = commandDispatcher.parse(command, commandSourceStack);

        try {
            Object result = handleParse(parseResults);
            if (!(result instanceof Boolean)){
                if (result == ExecuteResult.UNKNOWN_COMMAND){
                    return legacyCommandManager.execute(commandSender, command);
                }

                CommandSyntaxException e = (CommandSyntaxException) result;
                commandSender.sendMessage(e.getRawMessage());
                return ExecuteResult.COMMAND_ERROR;
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        commandDispatcher.execute(command, commandSourceStack);
        return ExecuteResult.EXECUTED;
    }

    private Object handleParse(ParseResults<CommandSourceStack> parseResults) throws CommandSyntaxException {
        if (parseResults.getReader().canRead()) {
            if (parseResults.getExceptions().size() == 1) {
                return parseResults.getExceptions().values().iterator().next();
            } else if (parseResults.getContext().getRange().isEmpty()) {
//                return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
                return ExecuteResult.UNKNOWN_COMMAND;
            } else {
                return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
            }
        }
        return true;
    }

    public void dispatch(@NotNull EntityCommandSender entityCommandSender, @NotNull String command){
        dispatch(entityCommandSender, entityCommandSender.getLocation(), command);
    }
    public void dispatch(@NotNull ConsoleCommandSender commandSender, @NotNull String command){
        dispatch(commandSender, null, command);
    }

    public CompletableFuture<SuggestionResult> suggest(CommandSender commandSender, String command, int cursor) {
        CompletableFuture<SuggestionResult> legacy = suggestLegacy(commandSender, command, cursor);
        CompletableFuture<SuggestionResult> modern = suggestModern(commandSender, command, cursor);

        return legacy.thenCombine(modern, this::mergeSuggestions);
    }

    private SuggestionResult mergeSuggestions(SuggestionResult legacyResult, SuggestionResult modernResult) {
        return new SuggestionResult(legacyResult, modernResult);
    }


    private CompletableFuture<SuggestionResult> suggestLegacy(CommandSender commandSender, String command, int cursor){
        return CompletableFuture.supplyAsync(()->{
            String[] suggestions = legacyCommandManager.getSuggestions(commandSender, command, cursor);
            StringRange range = StringRange.at(cursor);
            return new SuggestionResult(new Suggestions(range, Arrays.stream(suggestions).map(s->new Suggestion(range, s)).collect(Collectors.toList())));
        });
    }

    private CompletableFuture<SuggestionResult> suggestModern(CommandSender commandSender, String command, int cursor){
        if (!command.contains(" ")) {
            return CompletableFuture.supplyAsync(()->{
                List<String> suggestions = new ArrayList<>();
                commandDispatcher.getRoot()
                        .getChildren()
                        .forEach(child->{
                            suggestions.add(child.getName());
                        });
                StringRange range = StringRange.at(cursor);
                return new SuggestionResult(
                        new Suggestions(range ,suggestions.stream().map(s-> new Suggestion(range, s)).collect(Collectors.toList())));
            });
        }
        CommandSourceStack sourceStack = new CommandSourceStack(commandSender);
        ParseResults<CommandSourceStack> parseResults = commandDispatcher.parse(command, sourceStack);
        try {
            Object result = handleParse(parseResults);
            if (result instanceof Boolean){
                return handleSuggestions(sourceStack, parseResults, cursor);
            } else {
                boolean error = false;
                if (result == ExecuteResult.UNKNOWN_COMMAND){
                    error = true;
                } else if (result instanceof CommandSyntaxException){
                    CommandSyntaxException err = (CommandSyntaxException) result;
                    error = parseResults.getContext().getRange().isEmpty();
                }
                if (error){
                    return CompletableFuture.supplyAsync(()->{
                        String[] suggestions = legacyCommandManager.getSuggestions(commandSender, command, cursor);
                        if (suggestions == null){
                            return new SuggestionResult(new LiteralMessage(""));
                        }
                        List<String> suggestionsList = Arrays.stream(suggestions).collect(Collectors.toList());
                        StringRange range = StringRange.at(cursor);

                        return new SuggestionResult(
                                new Suggestions(
                                    range,
                                    suggestionsList.stream().map(s->new Suggestion(range, s)).collect(Collectors.toList())
                                )
                        );
                    });
                }
                if (result instanceof CommandSyntaxException){
                    CommandSyntaxException err = (CommandSyntaxException) result;
                    Message message = err.getRawMessage();
                    if (message == null){
                        message = LegacyBrigadier.legacyMessage("Error while trying to parse suggestions at "+ err.getCursor() + ": " + err.getContext());
                    }
                    return CompletableFuture.completedFuture(new SuggestionResult(message));
                }
                return CompletableFuture.supplyAsync(()->new SuggestionResult(new LiteralMessage("Unknown Command")));
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<SuggestionResult> handleSuggestions(CommandSourceStack sourceStack, ParseResults<CommandSourceStack> parseResults, int cursor){
        return commandDispatcher.getCompletionSuggestions(parseResults, cursor)
                .thenApply(s->{
                    if (s == null){
                        return new SuggestionResult(LegacyBrigadier.legacyMessage("§4Error trying to parse suggestions!"));
                    }
                    return new SuggestionResult(s);
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> command(String name){
        return literal(name);
    }
}
