package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;
import net.minecraft.client.util.Window;

public class ResolutionEvent extends CancellableEvent {
    public final Window window;

    public ResolutionEvent(Window window) {
        this.window = window;
    }
}
