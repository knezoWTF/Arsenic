package io.arsenic.event;

import meteordevelopment.orbit.ICancellable;

public class CancellableEvent implements ICancellable {
    private boolean cancelled = false;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}