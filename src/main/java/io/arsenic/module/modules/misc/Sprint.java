package io.arsenic.module.modules.misc;

import io.arsenic.event.events.TickListener;
import io.arsenic.module.Category;
import io.arsenic.module.Module;

public final class Sprint extends Module implements TickListener {
    public Sprint() {
        super("Sprint", "Keeps you sprinting at all times", -1, Category.MISC);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        super.onDisable();
    }

    @Override
    public void onTick() {
        mc.player.setSprinting(mc.player.input.pressingForward);
    }
}
