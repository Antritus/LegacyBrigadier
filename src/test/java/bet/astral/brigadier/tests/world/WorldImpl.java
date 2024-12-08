package bet.astral.brigadier.tests.world;

import bet.astral.brigadier.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WorldImpl implements World {
    private final String name = "world-"+ ThreadLocalRandom.current().nextInt(1000, 9999);
    private final UUID uniqueId = UUID.randomUUID();

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}
