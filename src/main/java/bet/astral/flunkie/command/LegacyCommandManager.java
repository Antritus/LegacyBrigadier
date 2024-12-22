package bet.astral.flunkie.command;

import bet.astral.flunkie.command.sender.CommandSender;

public interface LegacyCommandManager {
    boolean commandExists(CommandSender sender, String command);
    int execute(CommandSender sender, String command);
    String[] getSuggestions(CommandSender sender, String command, int cursor);
}
