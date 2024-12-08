package bet.astral.brigadier.command.sender;

import bet.astral.brigadier.world.Location;

public interface BlockCommandSender extends CommandSender {
    Location getLocation();
}
