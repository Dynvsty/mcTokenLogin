package dev.majanito.mixin;

import dev.majanito.screens.EditAccountScreen;
import dev.majanito.screens.LoginScreen;
import dev.majanito.utils.APIUtils;
import dev.majanito.utils.SessionUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        int loginButtonX = this.width - 90;
        int editAccountButtonX = this.width - 180;
        int buttonY = 5;
        int buttonWidth = 80;
        int buttonHeight = 20;

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
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String username = SessionUtils.getUsername();

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

        Component display = Component
            .literal("User: ")
            .append(Component.literal(username).withStyle(ChatFormatting.WHITE))
            .append(Component.literal(" | ")
            .withStyle(ChatFormatting.DARK_GRAY))
            .append(statusText);

        context.drawString(this.font, display, 5, 10, 0xFFFFFFFF, false);
    }
}
