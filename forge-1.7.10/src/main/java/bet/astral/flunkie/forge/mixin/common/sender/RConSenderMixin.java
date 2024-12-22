package bet.astral.flunkie.forge.mixin.common.sender;

import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import com.mojang.brigadier.Message;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RConConsoleSource.class)
public abstract class RConSenderMixin implements CommandSender {
    @Shadow public abstract void addChatMessage(IChatComponent p_145747_1_);

    @Override
    public void sendMessage(Message message) {
        if (message instanceof IChatComponent){
            addChatMessage((IChatComponent) message);
        } else {
            addChatMessage(LegacyBrigadierV1_7_10.convertMessageToComponent(message));
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
