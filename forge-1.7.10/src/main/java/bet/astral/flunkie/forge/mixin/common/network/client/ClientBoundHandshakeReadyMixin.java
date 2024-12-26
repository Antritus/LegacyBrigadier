package bet.astral.flunkie.forge.mixin.common.network.client;

import bet.astral.flunkie.LegacyBrigadier;
import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(S02PacketLoginSuccess.class)
public class ClientBoundHandshakeReadyMixin {
    @Inject(
            method = "processPacket(Lnet/minecraft/network/INetHandler;)V",
            at = @At("HEAD")
    )
    public void processPacket(INetHandler p_148833_1_, CallbackInfo ci){
        LegacyBrigadier.getInstance()
                .getPacketHandlerClient()
                .onConnect();
    }
}
