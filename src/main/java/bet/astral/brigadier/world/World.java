package bet.astral.brigadier.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface World {
    @Nullable
    UUID getUniqueId();
    @NotNull
    String getName();
}
