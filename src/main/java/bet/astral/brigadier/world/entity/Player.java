package bet.astral.brigadier.world.entity;

import bet.astral.brigadier.command.sender.EntityCommandSender;
import bet.astral.brigadier.world.Location;
import com.mojang.brigadier.Message;
import org.jetbrains.annotations.NotNull;

public interface Player extends Entity, EntityCommandSender {
    Location getLocation();

    void sendMessage(@NotNull Message message);
}
