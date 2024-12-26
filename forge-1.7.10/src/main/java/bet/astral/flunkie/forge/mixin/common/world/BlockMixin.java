package bet.astral.flunkie.forge.mixin.common.world;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.registry.Identifier;
import com.mojang.brigadier.Message;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.block.Block.getIdFromBlock;

@Mixin(Block.class)
public abstract class BlockMixin implements bet.astral.flunkie.world.Block {
    @Shadow private String unlocalizedName;

    @Override
    public Identifier getIdentifier() {
        return Identifier.of(Block.blockRegistry.getNameForObject(this));
    }

    @Override
    public int getId() {
        return getIdFromBlock((Block) (Object) this);
    }

    @Override
    public Message getTranslation() {
        return LegacyBrigadier.translationMessage(unlocalizedName);
    }
}
