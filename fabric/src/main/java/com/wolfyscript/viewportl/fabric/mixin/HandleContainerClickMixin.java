package com.wolfyscript.viewportl.fabric.mixin;

import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class HandleContainerClickMixin {

    @Inject(at = @At(value = "HEAD"), method = "handleContainerClick")
    private void handleCustomContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {



    }

}
