package io.arsenic;

import net.fabricmc.api.ModInitializer;

import java.io.IOException;

public final class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		try {
			new Arsenic();
		} catch (InterruptedException | IOException ignored) {}
	}
}
