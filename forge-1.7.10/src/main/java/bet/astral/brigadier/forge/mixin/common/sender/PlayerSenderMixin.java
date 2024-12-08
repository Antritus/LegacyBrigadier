package bet.astral.brigadier.forge.mixin.common.sender;

import bet.astral.brigadier.forge.world.EntityRotation;
import bet.astral.brigadier.world.Location;
import bet.astral.brigadier.world.entity.Entity;
import bet.astral.brigadier.world.entity.Player;
import com.mojang.brigadier.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static bet.astral.brigadier.forge.LegacyBrigadierV1_7_10.convertMessageToComponent;

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
                (bet.astral.brigadier.world.World) getEntityWorld()
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
