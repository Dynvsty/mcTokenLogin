package xyz.villainsrule.utils;

import java.net.Proxy;
import java.util.Optional;
import java.util.UUID;

import com.mojang.authlib.Environment;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.server.Services;

import xyz.villainsrule.TokenLoginMod;

public class SessionUtils {
    @SuppressWarnings("null")
    public static User createSession(String username, String uuidString, String ssid) {
        if (uuidString.length() == 32)
            uuidString = uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20);

        return new User(username, UUID.fromString(uuidString), ssid, Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("null")
    public static User createSession(String username, UUID uuid, String ssid) {
        return new User(username, uuid, ssid, Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("null")
    public static void setSession(User session) {
        TokenLoginMod.currentSession = session;

        Environment environment = YggdrasilEnvironment.PROD.getEnvironment();
        YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, environment);
        MinecraftSessionService sessionService = authService.createMinecraftSessionService();

        YggdrasilUserApiService userAuthenticationService = new YggdrasilUserApiService(session.getAccessToken(), Proxy.NO_PROXY, environment);
        ProfileKeyPairManager keys = ProfileKeyPairManager.create(userAuthenticationService, session, Minecraft.getInstance().gameDirectory.toPath());
        Services mcServices = new Services(sessionService, authService.getServicesKeySet(), authService.createProfileRepository(), Minecraft.getInstance().services().nameToIdCache(),
                Minecraft.getInstance().services().profileResolver());

        TokenLoginMod.currentServices = mcServices;
        TokenLoginMod.currentKeyPairManager = keys;
    }

    public static void restoreSession() {
        TokenLoginMod.currentSession = TokenLoginMod.restorableSession;
        TokenLoginMod.currentServices = TokenLoginMod.restorableServices;
        TokenLoginMod.currentKeyPairManager = TokenLoginMod.restorableKeyPairManager;
    }
}
