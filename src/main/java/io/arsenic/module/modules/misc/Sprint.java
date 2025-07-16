package io.arsenic.module.modules.misc;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import meteordevelopment.orbit.EventHandler;

public final class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Keeps you sprinting at all times", -1, Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onTickEvent(TickEvent event) {
        mc.player.setSprinting(mc.player.input.pressingForward);
    }
}
