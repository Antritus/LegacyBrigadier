package bet.astral.flunkie.forge.commands;

import bet.astral.flunkie.command.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.player.EntityPlayerMP;

import static bet.astral.flunkie.command.CommandManager.command;

public class TabRequestCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(){
        return command("/request")
                .executes((s)->{
                    EntityPlayerMP playerMP = (EntityPlayerMP) s.getSource().getPlayer();

                    return 0;
                });
    }
}
