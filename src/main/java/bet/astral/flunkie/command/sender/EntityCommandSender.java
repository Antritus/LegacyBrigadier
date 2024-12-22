package bet.astral.flunkie.command.sender;

import bet.astral.flunkie.world.HasLocation;
import bet.astral.flunkie.world.Location;
import bet.astral.flunkie.world.entity.Entity;

public interface EntityCommandSender extends CommandSender, HasLocation {
    Location getLocation();
    Entity getEntity();
}
