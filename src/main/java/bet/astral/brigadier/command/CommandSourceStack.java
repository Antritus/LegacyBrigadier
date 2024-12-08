package bet.astral.brigadier.command;

import bet.astral.brigadier.LegacyBrigadier;
import bet.astral.brigadier.command.sender.CommandSender;
import bet.astral.brigadier.world.Location;
import bet.astral.brigadier.world.entity.Entity;
import bet.astral.brigadier.world.entity.Player;
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
        this.location = new Location.LocationImpl(0, 0, 0, 0, 0, LegacyBrigadier.getInstance().getDefaultWorld());
        this.player = null;
        this.entity = null;
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
