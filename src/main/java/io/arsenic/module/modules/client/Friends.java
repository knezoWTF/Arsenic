package io.arsenic.module.modules.client;

import io.arsenic.Arsenic;
import io.arsenic.event.events.AttackEvent;
import io.arsenic.event.events.ButtonEvent;
import io.arsenic.event.events.HudEvent;
import io.arsenic.managers.FriendManager;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.KeybindSetting;
import io.arsenic.utils.RenderUtils;
import io.arsenic.utils.TextRenderer;
import io.arsenic.utils.WorldUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class Friends extends Module {
    private final KeybindSetting addFriendKey = new KeybindSetting("Friend Key", GLFW.GLFW_MOUSE_BUTTON_MIDDLE, false)
            .setDescription("Key to add/remove friends");
    public final BooleanSetting antiAttack = new BooleanSetting("Anti-Attack", false)
            .setDescription("Doesn't let you hit friends");
    public final BooleanSetting disableAimAssist = new BooleanSetting("Anti-Aim", false)
            .setDescription("Disables aim assist for friends");
    public final BooleanSetting friendStatus = new BooleanSetting("Friend Status", false)
            .setDescription("Tells you if you're aiming at a friend or not");

    private FriendManager manager;

    public Friends() {
        super("Friends", "This module makes it so you can't do certain stuff if you have a player friended!", -1, Category.CLIENT);
        addSettings(addFriendKey, antiAttack, disableAimAssist, friendStatus);
        setKey(-1);
    }

    @Override
    public void onEnable() {
        manager = Arsenic.INSTANCE.getFriendManager();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onButtonPressEvent(ButtonEvent event) {
        if(mc.player == null)
            return;

        if(mc.currentScreen != null)
            return;

        if(mc.crosshairTarget instanceof EntityHitResult hitResult) {
            Entity entity = hitResult.getEntity();

            if(entity instanceof PlayerEntity player) {
                if (event.button == addFriendKey.getKey() && event.action == GLFW.GLFW_PRESS) {
                    if(!manager.isFriend(player))
                        manager.addFriend(player);
                    else manager.removeFriend(player);
                }
            }
        }
    }

    @EventHandler
    private void onAttack(AttackEvent event) {
        if(!antiAttack.getValue())
            return;

        if(manager.isAimingOverFriend())
            event.cancel();
    }

    @EventHandler
    private void onRenderHud(HudEvent event) {
        if(!friendStatus.getValue())
            return;

        RenderUtils.unscaledProjection();
        if(WorldUtils.getHitResult(100) instanceof EntityHitResult hitResult) {
            Entity entity = hitResult.getEntity();
            DrawContext context = event.context;

            if(entity instanceof PlayerEntity player) {
                if(manager.isFriend(player)) {
                    TextRenderer.drawCenteredString("Player is friend", context, (mc.getWindow().getWidth() / 2), (mc.getWindow().getHeight() / 2) + 25, Color.GREEN.getRGB());
                }
            }
        }
        RenderUtils.scaledProjection();
    }
}
