package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;

public class MouseMoveEvent extends CancellableEvent {
    public final long windowHandle;
    public final double x, y;

    public MouseMoveEvent(long windowHandle, double x, double y) {
        this.windowHandle = windowHandle;
        this.x = x;
        this.y = y;
    }
}
