package io.arsenic.event.events;

import net.minecraft.client.gui.DrawContext;

public record HudEvent(DrawContext context, float delta) {
}
