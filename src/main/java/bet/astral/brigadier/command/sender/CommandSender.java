package bet.astral.brigadier.command.sender;

import com.mojang.brigadier.Message;

public interface CommandSender {
    void sendMessage(Message message);
    boolean isValid();
}
