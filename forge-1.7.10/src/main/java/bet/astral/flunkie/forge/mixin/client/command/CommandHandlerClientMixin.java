package bet.astral.flunkie.forge.mixin.client.command;

import bet.astral.flunkie.command.ExecuteResult;
import bet.astral.flunkie.command.LegacyCommandManager;
import bet.astral.flunkie.command.sender.BlockCommandSender;
import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.command.sender.EntityCommandSender;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.forge.commands.TestCommand;
import bet.astral.flunkie.world.Location;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.util.EnumChatFormatting.RED;

@Mixin(ClientCommandHandler.class)
public abstract class CommandHandlerClientMixin implements LegacyCommandManager {
    @Shadow
    protected abstract ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void serverCommandManager(CallbackInfo ci) {
        ((BaseCommandManager) this).getBrigadierManager().register(TestCommand.create());
    }

    @Override
    public int execute(CommandSender sender, String message) {
        message = message.trim();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        ICommandSender iSender = (ICommandSender) sender;
        try {
            String[] temp = message.split(" ");
            String[] args = new String[temp.length - 1];
            String commandName = temp[0];
            System.arraycopy(temp, 1, args, 0, args.length);
            ICommand icommand = (ICommand) ((CommandHandler) (Object) this).getCommands().get(commandName);

            if (icommand == null) {
                return 0;
            }
            if (icommand.canCommandSenderUseCommand(iSender)) {
                CommandEvent event = new CommandEvent(icommand, iSender, args);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    if (event.exception != null) {
                        throw event.exception;
                    }
                    return 0;
                }

                icommand.processCommand(iSender, args);
                return 1;
            } else {
                iSender.addChatMessage(format(RED, "commands.generic.permission"));
            }
        } catch (WrongUsageException wue) {
            iSender.addChatMessage(format(RED, "commands.generic.usage", format(RED, wue.getMessage(), wue.getErrorOjbects())));
        } catch (CommandException ce) {
            iSender.addChatMessage(format(RED, ce.getMessage(), ce.getErrorOjbects()));
        } catch (Throwable t) {
            iSender.addChatMessage(format(RED, "commands.generic.exception"));
            t.printStackTrace();
        }
        return -1;
    }

    @Overwrite
    public int executeCommand(ICommandSender sender, String message) {
        // Legacy Brigadier - Implement own logic to overwrite the 1.7.10 code
        CommandSender commandSender = (CommandSender) sender;
        Location location = null;
        if (commandSender instanceof EntityCommandSender) {
            location = ((EntityCommandSender) commandSender).getLocation();
        } else if (commandSender instanceof BlockCommandSender) {
            location = ((BlockCommandSender) commandSender).getLocation();
        }

        Object result = ((BaseCommandManager) this).getBrigadierManager().dispatch(commandSender, location, message);
        if (result == ExecuteResult.EXECUTED) {
            return 1;
        }
        if (result instanceof Integer) {
            return (int) result;
        }
        return -1;
    }
}