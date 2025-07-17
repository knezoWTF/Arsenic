package io.arsenic.event.events;

import net.minecraft.client.util.math.MatrixStack;

public record GameRenderEvent(MatrixStack matrices, float delta) {}
