package io.arsenic.module.modules.combat;

import io.arsenic.event.events.AttackEvent;
import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.InventoryUtils;
import io.arsenic.utils.MouseSimulation;
import io.arsenic.utils.WorldUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public final class ShieldDisabler extends Module {
	private final NumberSetting hitDelay = new NumberSetting("Hit Delay", 0, 20, 0, 1);
	private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0, 20, 0, 1);
	private final BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
	private final BooleanSetting stun = new BooleanSetting("Stun", false);
	private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulation", false);
	private final BooleanSetting requireHoldAxe = new BooleanSetting("Hold Axe", false);

	int previousSlot, hitClock, switchClock;

	public ShieldDisabler() {
		super("Shield Disabler",
				"Automatically disables your opponents shield",
				-1,
				Category.COMBAT);

		addSettings(switchDelay, hitDelay, switchBack, stun, clickSimulate, requireHoldAxe);
	}

	@Override
	public void onEnable() {
		hitClock = hitDelay.getValueInt();
		switchClock = switchDelay.getValueInt();
		previousSlot = -1;
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

		if(requireHoldAxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof AxeItem))
			return;

		if (mc.crosshairTarget instanceof EntityHitResult entityHit) {
			Entity entity = entityHit.getEntity();

			if (mc.player.isUsingItem())
				return;

			if (entity instanceof PlayerEntity player) {
				if (WorldUtils.isShieldFacingAway(player))
					return;

				if (player.isHolding(Items.SHIELD) && player.isBlocking()) {
					if (switchClock > 0) {
						if (previousSlot == -1)
							previousSlot = mc.player.getInventory().selectedSlot;

						switchClock--;
						return;
					}

					if (InventoryUtils.selectAxe()) {
						if (hitClock > 0) {
							hitClock--;
						} else {
							if (clickSimulate.getValue())
								MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_LEFT);

							WorldUtils.hitEntity(player, true);

							if (stun.getValue()) {
								if (clickSimulate.getValue())
									MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_LEFT);

								WorldUtils.hitEntity(player, true);
							}

							hitClock = hitDelay.getValueInt();
							switchClock = switchDelay.getValueInt();
						}
					}
				} else if (previousSlot != -1) {
					if (switchBack.getValue())
						InventoryUtils.setInvSlot(previousSlot);

					previousSlot = -1;
				}
			}
		}
	}

	@EventHandler
	private void onAttackEvent(AttackEvent event) {
		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
			event.cancel();
	}
}
