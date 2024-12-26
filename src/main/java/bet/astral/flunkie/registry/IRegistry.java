package bet.astral.flunkie.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface IRegistry<Key, Value> {
    @NotNull
    Optional<Value> get(Key key);
    @Nullable
    default Value getOrNull(Key value) {
        return get(value).orElse(null);
    }
    @NotNull
    Collection<? extends @NotNull Value> getRegistered();
}
