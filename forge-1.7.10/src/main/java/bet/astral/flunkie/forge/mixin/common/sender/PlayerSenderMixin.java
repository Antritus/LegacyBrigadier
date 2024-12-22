package bet.astral.flunkie.forge.mixin.common.sender;

import bet.astral.flunkie.forge.world.EntityRotation;
import bet.astral.flunkie.world.Location;
import bet.astral.flunkie.world.entity.Entity;
import bet.astral.flunkie.world.entity.Player;
import com.mojang.brigadier.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static bet.astral.flunkie.forge.LegacyBrigadierV1_7_10.convertMessageToComponent;

@Mixin(EntityPlayer.class)
public abstract class PlayerSenderMixin implements Player {
    @Shadow public abstract Vec3 getPosition(float par1);

    @Shadow public abstract World getEntityWorld();

    @Shadow public abstract void addChatComponentMessage(IChatComponent p_146105_1_);

    @Override
    public Location getLocation() {
        Vec3 vec3 = getPosition(1);
        EntityRotation rotation = (EntityRotation) this;

        return new Location.LocationImpl(
                vec3.xCoord,
                vec3.yCoord,
                vec3.zCoord,
                rotation.getYaw(),
                rotation.getPitch(),
                (bet.astral.flunkie.world.World) getEntityWorld()
        );
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void sendMessage(@org.jetbrains.annotations.NotNull Message message) {
        if (message instanceof IChatComponent){
            addChatComponentMessage((IChatComponent) message);
        } else {
            addChatComponentMessage(convertMessageToComponent(message));
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
