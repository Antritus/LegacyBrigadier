package bet.astral.flunkie.world;

import bet.astral.flunkie.registry.Identifier;
import com.mojang.brigadier.Message;

public interface Block {
    Identifier getIdentifier();
    int getId();
    Message getTranslation();
}
