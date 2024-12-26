package bet.astral.flunkie.forge.mixin.common.registry;

import bet.astral.flunkie.registry.INamespaceRegistry;
import bet.astral.flunkie.registry.Identifier;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(RegistryNamespaced.class)
public abstract class NamespaceRegistryMixin implements INamespaceRegistry<Object> {
    @Shadow public abstract Object getObject(String p_82594_1_);

    @Shadow protected ObjectIntIdentityMap underlyingIntegerMap;

    @Override
    public @NotNull Optional<Object> get(Identifier identifier) {
        return Optional.of(getObject(identifier.full()));
    }

    @Override
    public @NotNull Collection<?> getRegistered() {
        List<Object> list = new LinkedList<>();
        for (Object obj = underlyingIntegerMap.iterator().next(); underlyingIntegerMap.iterator().hasNext();){
            list.add(obj);
        }
        return list;
    }
}
