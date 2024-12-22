package bet.astral.flunkie.forge.commands;

import bet.astral.flunkie.command.CommandSourceStack;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import static bet.astral.flunkie.command.CommandManager.command;

public class TestCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(){
        return command("/test")
                .executes((s)->{
                    s.getSource().sendMessage(LegacyBrigadierV1_7_10.legacyMessage("§5HELLO"));
                    return 0;
                });
    }
}
