package bet.astral.brigadier.command;

import bet.astral.brigadier.LegacyBrigadier;
import bet.astral.brigadier.text.TranslationMessage;

import java.util.Arrays;
import java.util.List;

public class ExecutionResult {
    public static final ExecutionResult NON_VALID_SENDER = new ExecutionResult(Type.NON_VALID_SENDER, "commands.brigadier.invalid-sender");
    public static final ExecutionResult UNKNOWN_COMMAND = new ExecutionResult(Type.UNKNOWN_COMMAND, "commands.brigadier.unknown-command");
    public static final ExecutionResult UNKNOWN_ARGUMENT = new ExecutionResult(Type.UNKNOWN_ARGUMENT, "commands.brigadier-unknown-argument");
    public static final ExecutionResult SUCCESS = new ExecutionResult(Type.SUCCESS, "");
    private final Type type;
    private final String message;
    private final List<Object> placeholders;

    private ExecutionResult(Type type, String message, List<Object> placeholders) {
        this.type = type;
        this.message = message;
        this.placeholders = placeholders;
    }
    private ExecutionResult(Type type, String message) {
        this.type = type;
        this.message = message;
        this.placeholders = null;
    }

    public TranslationMessage getMessage(){
        return LegacyBrigadier.translationMessage(message, placeholders);
    }
    public String getMessageTranslation(){
        return message;
    }

    public ExecutionResult createResult(Object... placeholders){
        return new ExecutionResult(type, getMessageTranslation(), Arrays.asList(placeholders));
    }
    public ExecutionResult createResult(List<Object> placeholders){
        return new ExecutionResult(type, getMessageTranslation(), placeholders);
    }

    public enum Type {
        NON_VALID_SENDER,
        UNKNOWN_COMMAND,
        UNKNOWN_ARGUMENT,
        SUCCESS,
    }
}
