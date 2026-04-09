package xyz.villainsrule.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.server.Services;

@Mixin(Minecraft.class)
public interface IMinecraftServicesAccessor {
    @Accessor("services") @Mutable
    void setServices(Services services);

    @Accessor("profileKeyPairManager") @Mutable
    void setProfileKeyPairManager(ProfileKeyPairManager profileKeyPairManager);

    @Accessor("profileKeyPairManager")
    ProfileKeyPairManager profileKeyPairManager();
}
