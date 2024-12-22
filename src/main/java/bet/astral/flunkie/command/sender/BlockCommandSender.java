package bet.astral.flunkie.command.sender;

import bet.astral.flunkie.world.HasLocation;
import bet.astral.flunkie.world.Location;

public interface BlockCommandSender extends CommandSender, HasLocation {
    Location getLocation();
}
