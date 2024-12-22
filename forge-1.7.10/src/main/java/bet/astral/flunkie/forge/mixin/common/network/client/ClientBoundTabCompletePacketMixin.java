package bet.astral.flunkie.forge.mixin.common.network.client;

import bet.astral.flunkie.LegacyBrigadier;
import bet.astral.flunkie.network.client.ClientBoundLegacyTabCompletePacket;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S3APacketTabComplete;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(S3APacketTabComplete.class)
public abstract class ClientBoundTabCompletePacketMixin implements ClientBoundLegacyTabCompletePacket {
    @Shadow public abstract String[] func_149630_c();

    @Override
    public String[] getSuggestions() {
        return func_149630_c();
    }

    @Overwrite
    public void processPacket(INetHandler p_148833_1_)
    {
        LegacyBrigadier.getInstance().getPacketHandlerClient().onTabCompleteReceive(this);
    }
}
