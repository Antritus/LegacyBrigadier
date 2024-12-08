package bet.astral.brigadier.tests.command;

import bet.astral.brigadier.command.sender.ConsoleCommandSender;
import com.mojang.brigadier.Message;

public class DefaultConsole implements ConsoleCommandSender {
    public static DefaultConsole console = new DefaultConsole();
    @Override
    public void sendMessage(Message message) {
        System.out.println(message.getString());
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
