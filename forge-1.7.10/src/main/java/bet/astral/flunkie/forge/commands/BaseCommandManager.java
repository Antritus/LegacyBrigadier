package bet.astral.flunkie.forge.commands;

import bet.astral.flunkie.command.CommandManager;
import net.minecraft.command.ICommandSender;

public interface BaseCommandManager {
    CommandManager getBrigadierManager();
    void executeLegacy(ICommandSender sender, String command);
}
