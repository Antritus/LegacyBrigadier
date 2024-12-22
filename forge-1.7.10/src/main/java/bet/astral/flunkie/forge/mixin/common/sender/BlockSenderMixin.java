package bet.astral.flunkie.forge.mixin.common.sender;


import bet.astral.flunkie.command.sender.BlockCommandSender;
import bet.astral.flunkie.forge.LegacyBrigadierV1_7_10;
import bet.astral.flunkie.world.Location;
import com.mojang.brigadier.Message;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandBlockLogic.class)
public abstract class BlockSenderMixin implements BlockCommandSender {
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

    @Override
    public Location getLocation() {
        ICommandSender commandSender = (ICommandSender) this;
        ChunkCoordinates coordinates =  commandSender.getPlayerCoordinates();
        World world = commandSender.getEntityWorld();

        return new Location.LocationImpl(
                coordinates.posX,
                coordinates.posY,
                coordinates.posZ,
                0,
                0,
                (bet.astral.flunkie.world.World) world
        );
    }
}
