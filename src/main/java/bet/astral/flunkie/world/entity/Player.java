package bet.astral.flunkie.world.entity;

import bet.astral.flunkie.command.sender.EntityCommandSender;
import bet.astral.flunkie.world.Location;
import com.mojang.brigadier.Message;
import org.jetbrains.annotations.NotNull;

public interface Player extends Entity, EntityCommandSender {
    Location getLocation();

    void sendMessage(@NotNull Message message);

    boolean isUsingLegacyBrigadier();
    void setUsingLegacyBrigadier(boolean v);
}
