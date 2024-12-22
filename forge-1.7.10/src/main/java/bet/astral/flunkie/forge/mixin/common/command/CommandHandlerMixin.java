package bet.astral.flunkie.forge.mixin.common.command;

import bet.astral.flunkie.command.CommandManager;
import bet.astral.flunkie.command.ExecuteResult;
import bet.astral.flunkie.command.LegacyCommandManager;
import bet.astral.flunkie.command.sender.BlockCommandSender;
import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.command.sender.EntityCommandSender;
import bet.astral.flunkie.forge.commands.BaseCommandManager;
import bet.astral.flunkie.world.Location;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(CommandHandler.class)
public abstract class CommandHandlerMixin implements BaseCommandManager, LegacyCommandManager {
    @Shadow @Final private Map commandMap;

    @Shadow protected abstract int getUsernameIndex(ICommand p_82370_1_, String[] p_82370_2_);

    @Shadow @Final private static Logger logger;

    @Shadow public abstract List getPossibleCommands(ICommandSender p_71558_1_, String p_71558_2_);

    @Shadow public abstract Map getCommands();

    @Shadow @Final private Set commandSet;

    private CommandManager brigadierManager;

    public CommandManager getBrigadierManager() {
        if (brigadierManager == null){
            brigadierManager = new CommandManager(this);
        }
        return brigadierManager;
    }

    private static String[] dropFirstString(String[] p_71559_0_)
    {
        String[] astring1 = new String[p_71559_0_.length - 1];

        for (int i = 1; i < p_71559_0_.length; ++i)
        {
            astring1[i - 1] = p_71559_0_[i];
        }

        return astring1;
    }

    @Override
    public int execute(CommandSender sender, String p_71556_2_) {
        ICommandSender p_71556_1_ = (ICommandSender) sender;
        p_71556_2_ = p_71556_2_.trim();

        if (p_71556_2_.startsWith("/"))
        {
            p_71556_2_ = p_71556_2_.substring(1);
        }

        String[] astring = p_71556_2_.split(" ");
        String s1 = astring[0];
        astring = dropFirstString(astring);
        ICommand icommand = (ICommand)this.commandMap.get(s1);
        int i = this.getUsernameIndex(icommand, astring);
        int j = 0;
        ChatComponentTranslation chatcomponenttranslation;

        try
        {
            if (icommand == null)
            {
                throw new CommandNotFoundException();
            }

            if (icommand.canCommandSenderUseCommand(p_71556_1_))
            {
                CommandEvent event = new CommandEvent(icommand, p_71556_1_, astring);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 1;
                }

                if (i > -1)
                {
                    EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(p_71556_1_, astring[i]);
                    String s2 = astring[i];
                    EntityPlayerMP[] aentityplayermp1 = aentityplayermp;
                    int k = aentityplayermp.length;

                    for (int l = 0; l < k; ++l)
                    {
                        EntityPlayerMP entityplayermp = aentityplayermp1[l];
                        astring[i] = entityplayermp.getCommandSenderName();

                        try
                        {
                            icommand.processCommand(p_71556_1_, astring);
                            ++j;
                        }
                        catch (CommandException commandexception1)
                        {
                            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception1.getMessage(), commandexception1.getErrorOjbects());
                            chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
                            p_71556_1_.addChatMessage(chatcomponenttranslation1);
                        }
                    }

                    astring[i] = s2;
                }
                else
                {
                    try
                    {
                        icommand.processCommand(p_71556_1_, astring);
                        ++j;
                    }
                    catch (CommandException commandexception)
                    {
                        chatcomponenttranslation = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorOjbects());
                        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                        p_71556_1_.addChatMessage(chatcomponenttranslation);
                    }
                }
            }
            else
            {
                ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
                p_71556_1_.addChatMessage(chatcomponenttranslation2);
            }
        }
        catch (WrongUsageException wrongusageexception)
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new Object[] {new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorOjbects())});
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
        }
        catch (CommandException commandexception2)
        {
            chatcomponenttranslation = new ChatComponentTranslation(commandexception2.getMessage(), commandexception2.getErrorOjbects());
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
        }
        catch (Throwable throwable)
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
            logger.error("Couldn\'t process command: \'" + p_71556_2_ + "\'", throwable);
        }

        return j;
    }

    @Overwrite
    public int executeCommand(ICommandSender p_71556_1_, String p_71556_2_) {
        // Legacy Brigadier - Implement own logic to overwrite the 1.7.10 code
        if (p_71556_2_.startsWith("/")){
            CommandSender commandSender = (CommandSender) p_71556_1_;
            Location location = null;
            if (commandSender instanceof EntityCommandSender){
                location = ((EntityCommandSender) commandSender).getLocation();
            } else if (commandSender instanceof BlockCommandSender){
                location = ((BlockCommandSender) commandSender).getLocation();
            }

            Object result = ((BaseCommandManager) this).getBrigadierManager().dispatch(commandSender, location, p_71556_2_);
            if (result==ExecuteResult.EXECUTED || result==ExecuteResult.COMMAND_ERROR){
                return 1;
            }
            if (result instanceof Integer){
                return (int) result;
            }
        }
        // Legacy brigadier end
        return -1;
    }

    @Override
    public String[] getSuggestions(CommandSender sender, String command, int cursor) {
        ArrayList<String> completions = (ArrayList<String>) MinecraftServer.getServer().
                getPossibleCompletions((ICommandSender) sender, command);

        if (completions == null){
            return new String[0];
        }
        return completions.toArray(new String[0]);
    }

    @Override
    public boolean commandExists(CommandSender sender, String command) {
        String cmd = command;
        if (command.contains(" ")){
            cmd = command.split(" ")[0];
        }

        return commandMap.get(cmd) != null;
    }
}
