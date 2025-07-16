package io.arsenic.module.modules.misc;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.KeybindSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.InventoryUtils;
import io.arsenic.utils.KeyUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public final class KeyPearl extends Module {
    private final KeybindSetting activateKey = new KeybindSetting("Activate Key", -1, false);
    private final NumberSetting delay = new NumberSetting("Delay", 0, 20, 0, 1);
    private final BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0, 20, 0, 1)
            .setDescription("Delay after throwing pearl before switching back");

    private boolean active, hasActivated;
    private int clock, previousSlot, switchClock;

    public KeyPearl() {
        super("Key Pearl", "Switches to an ender pearl and throws it when you press a bind", -1, Category.MISC);
        addSettings(activateKey, delay, switchBack, switchDelay);
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onTickEvent(TickEvent event) {
        if(mc.currentScreen != null)
            return;

        if(KeyUtils.isKeyPressed(activateKey.getKey())) {
            active = true;
        }

        if(active) {
            if(previousSlot == -1)
                previousSlot = mc.player.getInventory().selectedSlot;

            InventoryUtils.selectItemFromHotbar(Items.ENDER_PEARL);

            if(clock < delay.getValueInt()) {
                clock++;
                return;
            }

            if(!hasActivated) {
                ActionResult result = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (result.isAccepted() && result.shouldSwingHand())
                    mc.player.swingHand(Hand.MAIN_HAND);

                hasActivated = true;
            }

            if(switchBack.getValue())
                switchBack();
            else reset();
        }
    }

    private void switchBack() {
        if(switchClock < switchDelay.getValueInt()) {
            switchClock++;
            return;
        }

        InventoryUtils.setInvSlot(previousSlot);
        reset();
    }

    private void reset() {
        previousSlot = -1;
        clock = 0;
        switchClock = 0;
        active = false;
        hasActivated = false;
    }
}
