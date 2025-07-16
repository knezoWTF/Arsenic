package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;
import io.arsenic.event.Listener;

import java.util.ArrayList;

public interface ItemUseListener extends Listener {
	void onItemUse(ItemUseEvent event);

	class ItemUseEvent extends CancellableEvent<ItemUseListener> {
		@Override
		public void fire(ArrayList<ItemUseListener> listeners) {
			listeners.forEach(e -> e.onItemUse(this));
		}

		@Override
		public Class<ItemUseListener> getListenerType() {
			return ItemUseListener.class;
		}
	}
}
