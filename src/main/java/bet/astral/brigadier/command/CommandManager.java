package bet.astral.brigadier.command;

import bet.astral.brigadier.command.sender.CommandSender;
import bet.astral.brigadier.command.sender.ConsoleCommandSender;
import bet.astral.brigadier.command.sender.EntityCommandSender;
import bet.astral.brigadier.world.Location;
import bet.astral.brigadier.world.entity.Entity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandManager {
    private final CommandDispatcher<CommandSourceStack> commandDispatcher = new CommandDispatcher<>();

    public <S extends CommandSourceStack> void register(LiteralArgumentBuilder<S> command){
        commandDispatcher.register((LiteralArgumentBuilder<CommandSourceStack>) command);
    }

    public void dispatch(@NotNull CommandSender commandSender, @Nullable Location location, @NotNull String command) {
        try {
            ExecutionResult result = handleExecution(commandSender, location, command);
            if (result == ExecutionResult.SUCCESS){
                return;
            }

            Message message = result.getMessage();
            commandSender.sendMessage(message);

        } catch (CommandSyntaxException e) {
            if (e.getCursor()==1){
                handleException(commandSender, command, ExecutionResult.UNKNOWN_COMMAND);
                return;
            }
        }
    }

    private void handleException(CommandSender commandSender, String command, ExecutionResult result){
        commandSender.sendMessage(result.createResult(command).getMessage());
    }
    private ExecutionResult handleExecution(@NotNull CommandSender commandSender, @Nullable Location location, @NotNull String command) throws CommandSyntaxException {
        if (!commandSender.isValid()){
            return ExecutionResult.NON_VALID_SENDER;
        }

        if (command.isEmpty()){
            return null;
        }
        CommandSourceStack commandSourceStack = new CommandSourceStack(commandSender, location, commandSender instanceof Entity ? (Entity) commandSender : null);
        ParseResults<CommandSourceStack> parseResults = commandDispatcher.parse(command, commandSourceStack);
        System.out.println(parseResults.getExceptions().size());

        try {
            Object result = handleParse(parseResults, command);
            if (!(result instanceof Boolean)){
                return (ExecutionResult) result;
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        commandDispatcher.execute(command, commandSourceStack);
        return ExecutionResult.SUCCESS;
    }

    private Object handleParse(ParseResults<CommandSourceStack> parseResults, String commandName) throws CommandSyntaxException {
        if (parseResults.getReader().canRead()) {
            if (parseResults.getExceptions().size() == 1) {
                throw parseResults.getExceptions().values().iterator().next();
            } else if (parseResults.getContext().getRange().isEmpty()) {
                return handleUnknownCommand(commandName);
            } else {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
            }
        }
        return true;
    }

    private ExecutionResult handleUnknownCommand(String command){
        return ExecutionResult.UNKNOWN_COMMAND.createResult(command);
    }

    public void dispatch(@NotNull EntityCommandSender entityCommandSender, @NotNull String command){
        dispatch(entityCommandSender, entityCommandSender.getLocation(), command);
    }
    public void dispatch(@NotNull ConsoleCommandSender commandSender, @NotNull String command){
        dispatch(commandSender, null, command);
    }

    public SuggestionResult suggest(CommandSender commandSender, String command, int cursor){
        return null;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> command(String name){
        return literal(name);
    }
}
