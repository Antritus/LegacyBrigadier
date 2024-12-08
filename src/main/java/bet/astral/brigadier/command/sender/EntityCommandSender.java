package bet.astral.brigadier.command.sender;

import bet.astral.brigadier.world.Location;
import bet.astral.brigadier.world.entity.Entity;

public interface EntityCommandSender extends CommandSender{
    Location getLocation();
    Entity getEntity();
}
