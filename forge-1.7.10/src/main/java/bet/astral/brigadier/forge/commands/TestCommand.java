package bet.astral.brigadier.forge.commands;

import bet.astral.brigadier.command.CommandSourceStack;
import bet.astral.brigadier.forge.LegacyBrigadierV1_7_10;
import bet.astral.brigadier.forge.network.client.ServerBoundTabCompletePacket;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.player.EntityPlayerMP;

import static bet.astral.brigadier.command.CommandManager.command;

public class TestCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(){
        return command("/test")
                .executes((s)->{
                    LegacyBrigadierV1_7_10.network
                            .sendTo(
                                    new ServerBoundTabCompletePacket(
                                            "/test",
                                            1
                                    ),
                                    ((EntityPlayerMP) s.getSource().getPlayer())
                            );
                    return 0;
                });
    }
}
