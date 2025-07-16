package io.arsenic.event.events;

import net.minecraft.client.util.math.MatrixStack;

public class GameRenderEvent {
    public final MatrixStack matrices;
    public final float delta;

    public GameRenderEvent(MatrixStack matrices, float delta) {
        this.matrices = matrices;
        this.delta = delta;
    }
}
