package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.InventoryUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public final class AutoPot extends Module {
	private final NumberSetting minHealth = new NumberSetting("Min Health", 1, 20, 10, 1);
	private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0, 10, 0, 1);
	private final NumberSetting throwDelay = new NumberSetting("Throw Delay", 0, 10, 0, 1);
	private final BooleanSetting goToPrevSlot = new BooleanSetting("Switch Back", true);
	private final BooleanSetting lookDown = new BooleanSetting("Look Down", true);

	private int switchClock, throwClock, prevSlot;
	private float prevPitch;
	private boolean bool;

	public AutoPot() {
		super("Auto Pot",
				"Automatically throws health potions when low on health",
				-1,
				Category.COMBAT);

		addSettings(minHealth, switchDelay, throwDelay, goToPrevSlot, lookDown);
	}

	private void reset() {
		switchClock = 0;
		throwClock = 0;
		prevSlot = -1;
		prevPitch = -1;
	}

	@Override
	public void onEnable() {
		reset();
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

		if ((mc.player.getHealth() <= minHealth.getValueFloat() || bool)) {

			if (bool && mc.player.getHealth() >= mc.player.getMaxHealth()) {
				bool = false;
				return;
			}

			if (!InventoryUtils.isThatSplash(StatusEffects.INSTANT_HEALTH.value(), 1, 1, mc.player.getMainHandStack())) {
				if (switchClock < switchDelay.getValue()) {
					switchClock++;
					return;
				}

				if (goToPrevSlot.getValue() && prevSlot == -1) prevSlot = mc.player.getInventory().selectedSlot;
				if (lookDown.getValue() && prevPitch == -1) prevPitch = mc.player.getPitch();

				int potSlot = InventoryUtils.findSplash(StatusEffects.INSTANT_HEALTH.value(), 1, 1);

				if (potSlot != -1) {
					InventoryUtils.setInvSlot(potSlot);

					switchClock = 0;
				}
			}

			if (InventoryUtils.isThatSplash(StatusEffects.INSTANT_HEALTH.value(), 1, 1, mc.player.getMainHandStack())) {
				if (throwClock < throwDelay.getValue()) {
					throwClock++;
					return;
				}

				if (lookDown.getValue())
					mc.player.setPitch(90F);

				ActionResult actionResult = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
				if (actionResult.shouldSwingHand())
					mc.player.swingHand(Hand.MAIN_HAND);

				throwClock = 0;
			}
		} else if (prevSlot != -1 || prevPitch != -1) {
			InventoryUtils.setInvSlot(prevSlot);
			prevSlot = -1;

			mc.player.setPitch(prevPitch);
			prevPitch = -1;
		}
	}
}