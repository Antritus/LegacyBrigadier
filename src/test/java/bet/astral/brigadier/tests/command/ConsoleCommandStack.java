package bet.astral.brigadier.tests.command;

import bet.astral.brigadier.command.sender.CommandSender;
import bet.astral.brigadier.command.CommandSourceStack;

public class ConsoleCommandStack extends CommandSourceStack {
    public ConsoleCommandStack(CommandSender commandSender) {
        super(commandSender);
    }
}
