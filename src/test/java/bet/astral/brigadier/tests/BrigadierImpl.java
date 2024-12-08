package bet.astral.brigadier.tests;

import bet.astral.brigadier.LegacyBrigadier;
import bet.astral.brigadier.command.CommandManager;
import bet.astral.brigadier.command.sender.ConsoleCommandSender;
import bet.astral.brigadier.tests.command.DefaultConsole;
import bet.astral.brigadier.tests.text.MessageImpl;
import bet.astral.brigadier.tests.text.TranslationMessageImpl;
import bet.astral.brigadier.tests.world.WorldImpl;
import bet.astral.brigadier.text.JsonMessage;
import bet.astral.brigadier.text.TranslationMessage;
import bet.astral.brigadier.world.World;
import com.mojang.brigadier.Message;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static bet.astral.brigadier.command.CommandManager.command;

public class BrigadierImpl extends LegacyBrigadier {
    private final World defaultWorld = new WorldImpl();

    public BrigadierImpl(){
        LegacyBrigadier impl = getInstance();
        if (impl != null) {
            return;
        }
        Class<?> clazz = LegacyBrigadier.class;
        try {
            Field field = clazz.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public World getDefaultWorld() {
        return defaultWorld;
    }

    @Override
    public TranslationMessage createTranslationMessage(String message) {
        return new TranslationMessageImpl(message);
    }

    @Override
    public TranslationMessage createTranslationMessage(String message, List<Object> placeholders) {
        return new TranslationMessageImpl(message, placeholders);
    }

    @Override
    public TranslationMessage createTranslationMessage(String message, Object... placeholders) {
        return new TranslationMessageImpl(message, placeholders);
    }

    @Override
    public Message createLegacyMessage(String message) {
        return new MessageImpl(message);
    }

    @Override
    public JsonMessage createJsonMessage(String message) {
        return new MessageImpl(message);
    }

    public static void main(String[] args){
        new BrigadierImpl();

        CommandManager commandManager = new CommandManager();

        AtomicBoolean stop = new AtomicBoolean(false);
        commandManager.register(
                command("helloworld")
                        .executes(c->{
                            c.getSource().getSender().sendMessage(new MessageImpl("Hello World!"));
                            return 1;
                        })
        );
        commandManager.register(
                command("stop")
                        .executes(c->{
                            stop.set(true);
                            return 1;
                        })
        );

        Scanner scanner = new Scanner(System.in);

        ConsoleCommandSender commandSender = DefaultConsole.console;

        while (!stop.get()){
            String in = scanner.nextLine();

            commandManager.dispatch(commandSender, in);
        }
    }
}
