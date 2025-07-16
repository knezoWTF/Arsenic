package io.arsenic.module.modules.misc;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;

public final class NoJumpDelay extends Module {
	public NoJumpDelay() {
		super("No Jump Delay",
				"Lets you jump faster, removing the delay",
				-1,
				Category.MISC);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	private void onTickEvent(TickEvent event) {
		if (mc.currentScreen != null)
			return;

		if (!mc.player.isOnGround())
			return;

		if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE) != GLFW.GLFW_PRESS)
			return;

		mc.options.jumpKey.setPressed(false);
		mc.player.jump();
	}
}
