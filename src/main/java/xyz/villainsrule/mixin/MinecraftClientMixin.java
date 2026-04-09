package xyz.villainsrule.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.server.Services;

import xyz.villainsrule.TokenLoginMod;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "getUser", at = @At("HEAD"), cancellable = true)
    private void onGetSession(CallbackInfoReturnable<User> cir) {
        if (TokenLoginMod.hasInit)
            cir.setReturnValue(TokenLoginMod.currentSession);
    }

    @Inject(method = "services", at = @At("HEAD"), cancellable = true)
    private void onGetServices(CallbackInfoReturnable<Services> cir) {
        if (TokenLoginMod.hasInit)
            cir.setReturnValue(TokenLoginMod.currentServices);
    }

    @Inject(method = "getProfileKeyPairManager", at = @At("HEAD"), cancellable = true)
    private void onGetProfileKeyPairManager(CallbackInfoReturnable<ProfileKeyPairManager> cir) {
        if (TokenLoginMod.hasInit) {
            // keyPairManager is init'd after hasInit is called, and
            // fixing this would require fabric API to also be here,
            // which i removed a bit back and would irk me to readd
            if (TokenLoginMod.currentKeyPairManager == null) {
                TokenLoginMod.currentKeyPairManager = ((IMinecraftServicesAccessor) Minecraft.getInstance()).profileKeyPairManager();
                TokenLoginMod.restorableKeyPairManager = TokenLoginMod.currentKeyPairManager;
            }

            cir.setReturnValue(TokenLoginMod.currentKeyPairManager);
        }
    }
}
