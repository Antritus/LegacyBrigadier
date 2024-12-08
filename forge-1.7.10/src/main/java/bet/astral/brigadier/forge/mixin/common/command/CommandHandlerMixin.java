package bet.astral.brigadier.forge.mixin.common.command;

import bet.astral.brigadier.command.CommandManager;
import bet.astral.brigadier.command.CommandSourceStack;
import bet.astral.brigadier.command.sender.BlockCommandSender;
import bet.astral.brigadier.command.sender.CommandSender;
import bet.astral.brigadier.command.sender.EntityCommandSender;
import bet.astral.brigadier.forge.commands.BaseCommandManager;
import bet.astral.brigadier.world.Location;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(CommandHandler.class)
public abstract class CommandHandlerMixin implements BaseCommandManager {
    @Shadow @Final private Map commandMap;

    @Shadow protected abstract int getUsernameIndex(ICommand p_82370_1_, String[] p_82370_2_);

    @Shadow @Final private static Logger logger;
    private CommandManager brigadierManager = new CommandManager();

    public CommandManager getBrigadierManager() {
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
    @Overwrite
    public int executeCommand(ICommandSender p_71556_1_, String p_71556_2_) {
        p_71556_2_ = p_71556_2_.trim();

        // Legacy Brigadier - Implement own logic to overwrite the 1.7.10 code
        if (p_71556_2_.startsWith("/")){

            CommandSender commandSender = (CommandSender) p_71556_1_;
            Location location = null;
            if (commandSender instanceof EntityCommandSender){
                location = ((EntityCommandSender) commandSender).getLocation();
            } else if (commandSender instanceof BlockCommandSender){
                location = ((BlockCommandSender) commandSender).getLocation();
            }

            ((BaseCommandManager) this).getBrigadierManager().dispatch(commandSender, location, p_71556_2_);

            return 1;
        }

        // Legacy brigadier end

        if (p_71556_2_.startsWith("/")) {
            p_71556_2_ = p_71556_2_.substring(1);
        }

        String[] astring = p_71556_2_.split(" ");
        String s1 = astring[0];
        astring = dropFirstString(astring);
        ICommand icommand = (ICommand) this.commandMap.get(s1);
        int i = this.getUsernameIndex(icommand, astring);
        int j = 0;
        ChatComponentTranslation chatcomponenttranslation;

        try {
            if (icommand == null) {
                throw new CommandNotFoundException();
            }

            if (icommand.canCommandSenderUseCommand(p_71556_1_)) {

                CommandEvent event = new CommandEvent(icommand, p_71556_1_, astring);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    if (event.exception != null) {
                        throw event.exception;
                    }
                    return 1;
                }

                if (i > -1) {
                    EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(p_71556_1_, astring[i]);
                    String s2 = astring[i];
                    EntityPlayerMP[] aentityplayermp1 = aentityplayermp;
                    int k = aentityplayermp.length;

                    for (int l = 0; l < k; ++l) {
                        EntityPlayerMP entityplayermp = aentityplayermp1[l];
                        astring[i] = entityplayermp.getCommandSenderName();

                        try {
                            icommand.processCommand(p_71556_1_, astring);
                            ++j;
                        } catch (CommandException commandexception1) {
                            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception1.getMessage(), commandexception1.getErrorOjbects());
                            chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
                            p_71556_1_.addChatMessage(chatcomponenttranslation1);
                        }
                    }

                    astring[i] = s2;
                } else {
                    try {
                        icommand.processCommand(p_71556_1_, astring);
                        ++j;
                    } catch (CommandException commandexception) {
                        chatcomponenttranslation = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorOjbects());
                        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                        p_71556_1_.addChatMessage(chatcomponenttranslation);
                    }
                }
            } else {
                ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.permission");
                chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
                p_71556_1_.addChatMessage(chatcomponenttranslation2);
            }
        } catch (WrongUsageException wrongusageexception) {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorOjbects()));
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
        } catch (CommandException commandexception2) {
            chatcomponenttranslation = new ChatComponentTranslation(commandexception2.getMessage(), commandexception2.getErrorOjbects());
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
        } catch (Throwable throwable) {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            p_71556_1_.addChatMessage(chatcomponenttranslation);
            logger.error("Couldn\'t process command: \'" + p_71556_2_ + "\'", throwable);
        }

        return j;
    }
}
