package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;
import net.minecraft.client.gui.DrawContext;

public class HudEvent extends CancellableEvent {
    public final DrawContext context;
    public final float delta;

    public HudEvent(DrawContext context, float delta) {
        this.context = context;
        this.delta = delta;
    }
}
