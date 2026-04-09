package xyz.villainsrule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.server.Services;
import net.fabricmc.api.ModInitializer;

public class TokenLoginMod implements ModInitializer {
    public static final String MOD_ID = "tklogin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean hasInit = false;

    public static User restorableSession;
    public static User currentSession;

    public static Services restorableServices;
    public static Services currentServices;

    public static ProfileKeyPairManager restorableKeyPairManager;
    public static ProfileKeyPairManager currentKeyPairManager;

    @Override
    public void onInitialize() {
        restorableSession = Minecraft.getInstance().getUser();
        currentSession = restorableSession;

        restorableServices = Minecraft.getInstance().services();
        currentServices = restorableServices;

        restorableKeyPairManager = Minecraft.getInstance().getProfileKeyPairManager();
        currentKeyPairManager = restorableKeyPairManager;

        hasInit = true;
    }
}
