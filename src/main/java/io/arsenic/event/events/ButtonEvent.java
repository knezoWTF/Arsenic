package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;

public class ButtonEvent extends CancellableEvent {
    public final int button, action;
    public final long window;

    public ButtonEvent(int button, long window, int action) {
        this.button = button;
        this.window = window;
        this.action = action;
    }

}
