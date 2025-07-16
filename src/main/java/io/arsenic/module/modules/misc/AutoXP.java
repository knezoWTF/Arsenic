package io.arsenic.module.modules.misc;

import io.arsenic.event.events.ItemUseEvent;
import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.MathUtils;
import io.arsenic.utils.MouseSimulation;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public final class AutoXP extends Module {
	private final NumberSetting delay = new NumberSetting("Delay", 0, 20, 0, 1);
	private final NumberSetting chance = new NumberSetting("Chance", 0, 100, 100, 1)
			.setDescription("Randomization");
	private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false)
			.setDescription("Makes the CPS hud think you're legit");
	int clock;

	public AutoXP() {
		super("Auto XP",
				"Automatically throws XP bottles for you",
				-1,
				Category.MISC);
		addSettings(delay, chance, clickSimulation);
	}

	@Override
	public void onEnable() {
		clock = 0;
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

		boolean dontThrow = clock != 0;

		int randomInt = MathUtils.randomInt(1, 100);

		if (mc.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE)
			return;

		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS)
			return;

		if (dontThrow)
			clock--;

		if (!dontThrow && randomInt <= chance.getValueInt()) {
			if (clickSimulation.getValue())
				MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

			ActionResult result = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
			if (result.isAccepted() && result.shouldSwingHand()) mc.player.swingHand(Hand.MAIN_HAND);

			clock = delay.getValueInt();
		}
	}

	@EventHandler
	private void onItemUseEvent(ItemUseEvent event) {
		if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
			event.cancel();
		}
	}

}
