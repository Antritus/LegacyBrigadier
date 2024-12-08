package bet.astral.brigadier.tests.text;

import bet.astral.brigadier.text.JsonMessage;
import com.mojang.brigadier.Message;

public class MessageImpl implements Message, JsonMessage {
    private final String string;

    public MessageImpl(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }
}
