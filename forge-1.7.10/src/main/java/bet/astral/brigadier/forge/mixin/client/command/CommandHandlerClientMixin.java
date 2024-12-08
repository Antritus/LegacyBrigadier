package bet.astral.brigadier.forge.mixin.client.command;

import bet.astral.brigadier.command.sender.BlockCommandSender;
import bet.astral.brigadier.command.sender.CommandSender;
import bet.astral.brigadier.command.sender.EntityCommandSender;
import bet.astral.brigadier.forge.commands.BaseCommandManager;
import bet.astral.brigadier.forge.commands.TestCommand;
import bet.astral.brigadier.world.Location;
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
public abstract class CommandHandlerClientMixin {
    @Shadow protected abstract ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args);
    @Inject(method = "<init>", at = @At("TAIL"))
    public void serverCommandManager(CallbackInfo ci){
        ((BaseCommandManager) this).getBrigadierManager().register(TestCommand.create());
    }

    @Overwrite
    public int executeCommand(ICommandSender sender, String message) {
        message = message.trim();

        // Legacy Brigadier - Implement own logic to overwrite the 1.7.10 code
        if (message.startsWith("!")){
            CommandSender commandSender = (CommandSender) sender;
            Location location = null;
            if (commandSender instanceof EntityCommandSender){
                location = ((EntityCommandSender) commandSender).getLocation();
            } else if (commandSender instanceof BlockCommandSender){
                location = ((BlockCommandSender) commandSender).getLocation();
            }

            ((BaseCommandManager) this).getBrigadierManager().dispatch(commandSender, location, message);

            return 1;
        }

        // Legacy brigadier end

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand icommand = (ICommand) ((CommandHandler) (Object)this).getCommands().get(commandName);

        try {
            if (icommand == null) {
                return 0;
            }

            if (icommand.canCommandSenderUseCommand(sender)) {
                CommandEvent event = new CommandEvent(icommand, sender, args);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    if (event.exception != null) {
                        throw event.exception;
                    }
                    return 0;
                }

                icommand.processCommand(sender, args);
                return 1;
            } else {
                sender.addChatMessage(format(RED, "commands.generic.permission"));
            }
        } catch (WrongUsageException wue) {
            sender.addChatMessage(format(RED, "commands.generic.usage", format(RED, wue.getMessage(), wue.getErrorOjbects())));
        } catch (CommandException ce) {
            sender.addChatMessage(format(RED, ce.getMessage(), ce.getErrorOjbects()));
        } catch (Throwable t) {
            sender.addChatMessage(format(RED, "commands.generic.exception"));
            t.printStackTrace();
        }

        return -1;
    }
}
