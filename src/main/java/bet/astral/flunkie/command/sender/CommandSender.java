package bet.astral.flunkie.command.sender;

import com.mojang.brigadier.Message;

public interface CommandSender {
    void sendMessage(Message message);
    boolean isValid();
}
