package bet.astral.flunkie.command;

import bet.astral.flunkie.LegacyBrigadier;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public interface Exceptions {
    DynamicCommandExceptionType UNKNOWN_BLOCK = new DynamicCommandExceptionType(obj-> LegacyBrigadier.jsonMessage("Unknown block '"+obj+"'"));
}
