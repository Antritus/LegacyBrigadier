package bet.astral.flunkie.command;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.command.sender.CommandSender;
import bet.astral.flunkie.world.HasLocation;
import bet.astral.flunkie.world.Location;
import bet.astral.flunkie.world.entity.Entity;
import bet.astral.flunkie.world.entity.Player;
import com.mojang.brigadier.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSourceStack {
    private final CommandSender commandSender;
    private final Location location;
    private final Entity entity;
    private final Player player;

    public CommandSourceStack(CommandSender commandSender){
        this.commandSender = commandSender;
        this.location = commandSender instanceof HasLocation ? ((HasLocation) commandSender).getLocation() : new Location.LocationImpl(0, 0, 0, 0, 0, LegacyBrigadier.getInstance().getDefaultWorld());
        this.player = commandSender instanceof Player ? (Player) commandSender : null;
        this.entity = commandSender instanceof Entity ? (Entity) commandSender : null;
    }

    public CommandSourceStack(CommandSender commandSender, Location location, Entity entity) {
        this.commandSender = commandSender;
        this.location = location;
        this.player = entity instanceof Player ? (Player) entity : null;
        this.entity = entity;
    }

    @NotNull
    public CommandSender getSender() {
        return commandSender;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Entity getEntity(){
        return entity;
    }

    public void sendMessage(Message message){
        getSender().sendMessage(message);
    }
}
