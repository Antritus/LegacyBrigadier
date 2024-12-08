package bet.astral.brigadier.forge.mixin.server.command;

import bet.astral.brigadier.command.CommandSourceStack;
import bet.astral.brigadier.forge.commands.BaseCommandManager;
import bet.astral.brigadier.forge.commands.TabRequestCommand;
import bet.astral.brigadier.forge.commands.TestCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.ServerCommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandManager.class)
public class ServerCommandManagerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void serverCommandManager(CallbackInfo ci){
        registerBrigadier(TestCommand.create());
        registerBrigadier(TabRequestCommand.create());
    }

    public void registerBrigadier(LiteralArgumentBuilder<CommandSourceStack> builder) {
        ((BaseCommandManager) this).getBrigadierManager().register(builder);
    }
}
