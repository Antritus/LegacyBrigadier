package bet.astral.flunkie.forge.mixin.common.world;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;


@Mixin(World.class)
public abstract class WorldMixin implements bet.astral.flunkie.world.World {
    @Shadow public abstract WorldInfo getWorldInfo();

    @NotNull
    @Override
    public String getName() {
        return getWorldInfo().getWorldName();
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return null;
    }
}
