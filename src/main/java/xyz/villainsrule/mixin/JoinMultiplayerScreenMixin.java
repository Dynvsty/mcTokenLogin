package xyz.villainsrule.mixin;

import org.jspecify.annotations.NonNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.fabricmc.loader.api.FabricLoader;

import xyz.villainsrule.screens.EditAccountScreen;
import xyz.villainsrule.screens.LoginScreen;
import xyz.villainsrule.utils.APIUtils;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    @Unique
    private static Boolean isSessionValid = null;
    @Unique
    private static boolean hasValidationStarted = false;

    protected JoinMultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        boolean meteorInstalled = FabricLoader.getInstance().isModLoaded("meteor-client");

        int buttonY = 3;
        int buttonWidth = 75;
        int buttonHeight = 20;
        int spacing = 2;
        int rightMargin = 5;

        int loginButtonX;
        int editAccountButtonX;

        if (meteorInstalled) {
            int meteorOffset = 77;
            loginButtonX = this.width - rightMargin - buttonWidth - meteorOffset;
            editAccountButtonX = loginButtonX - buttonWidth - spacing;
        } else {
            loginButtonX = this.width - rightMargin - buttonWidth;
            editAccountButtonX = loginButtonX - buttonWidth - spacing;
        }

        this.addRenderableWidget(Button.builder(Component.literal("Login"), button -> {
            this.minecraft.setScreen(new LoginScreen());
        }).bounds(loginButtonX, buttonY, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal("Edit Account"), button -> {
            this.minecraft.setScreen(new EditAccountScreen());
        }).bounds(editAccountButtonX, buttonY, buttonWidth, buttonHeight).build());

        isSessionValid = null;
        hasValidationStarted = false;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        String username = Minecraft.getInstance().getUser().getName();

        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;
            new Thread(() -> {
                isSessionValid = APIUtils.validateSession(this.minecraft.getUser().getAccessToken());
            }, "SessionValidationThread").start();
        }

        Component statusText;

        if (isSessionValid == null)
            statusText = Component.literal("[... Validating]").withStyle(ChatFormatting.GRAY);
        else if (isSessionValid)
            statusText = Component.literal("[✔] Valid").withStyle(ChatFormatting.GREEN);
        else
            statusText = Component.literal("[✘] Invalid").withStyle(ChatFormatting.RED);

        Component display =
                Component.literal("User: ").append(Component.literal(username).withStyle(ChatFormatting.WHITE)).append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY)).append(statusText);

        context.text(this.font, display, 5, 10, 0xFFFFFFFF, false);
    }
}
