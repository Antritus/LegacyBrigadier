package bet.astral.flunkie.forge.mixin.common.entity;

import bet.astral.flunkie.forge.world.EntityRotation;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements EntityRotation {
    @Shadow public float rotationYaw;

    @Shadow public float rotationPitch;

    @Override
    public float getYaw() {
        return rotationYaw;
    }

    @Override
    public float getPitch() {
        return rotationPitch;
    }
}
