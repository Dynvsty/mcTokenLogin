package dev.majanito.mixin;

import dev.majanito.SessionIDLoginMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "getUser", at = @At("HEAD"), cancellable = true)
    private void onGetSession(CallbackInfoReturnable<User> cir) {
        if (SessionIDLoginMod.overrideSession)
            cir.setReturnValue(SessionIDLoginMod.currentSession);
    }
}
