package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.mixin.HandledScreenMixin;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.ModeSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.InventoryUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public final class AutoPotRefill extends Module {
	public enum Mode {
		Auto, Hover
	}

	private final ModeSetting<Mode> mode = new ModeSetting<>("Mode", Mode.Auto, Mode.class);
	private final NumberSetting delay = new NumberSetting("Delay", 0, 10, 0, 1);

	private int clock;

	public AutoPotRefill() {
		super("Auto Pot Refill",
				"Refills your hotbar with potions",
				-1,
				Category.COMBAT);
		addSettings(mode, delay);
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
		if (mc.currentScreen instanceof InventoryScreen inventoryScreen) {
			if (mode.isMode(Mode.Hover)) {
				Slot focusedSlot = ((HandledScreenMixin) inventoryScreen).getFocusedSlot();

				if (focusedSlot == null)
					return;

				PlayerInventory inventory = mc.player.getInventory();

				int emptySlot = -1;
				for (int i = 0; i <= 8; i++) {
					if (inventory.getStack(i).isEmpty()) {
						emptySlot = i;
						break;
					}
				}

				if (emptySlot == -1)
					return;

				if (InventoryUtils.isThatSplash(StatusEffects.INSTANT_HEALTH.value(), 1, 1, focusedSlot.getStack())) {
					if (clock < delay.getValueInt()) {
						clock++;
						return;
					}

					mc.interactionManager.clickSlot(
							inventoryScreen.getScreenHandler().syncId,
							focusedSlot.getIndex(),
							emptySlot,
							SlotActionType.SWAP,
							mc.player);

					clock = 0;
				}
			}

			if (mode.isMode(Mode.Auto)) {
				int slot = InventoryUtils.findPot(StatusEffects.INSTANT_HEALTH.value(), 1, 1);

				if (slot != -1) {
					PlayerInventory inventory = mc.player.getInventory();

					int emptySlot = -1;
					for (int i = 0; i <= 8; i++) {
						if (inventory.getStack(i).isEmpty()) {
							emptySlot = i;
							break;
						}
					}

					if (emptySlot == -1) return;

					if (clock < delay.getValueInt()) {
						clock++;
						return;
					}

					mc.interactionManager.clickSlot(
							inventoryScreen.getScreenHandler().syncId,
							slot,
							emptySlot,
							SlotActionType.SWAP,
							mc.player);

					clock = 0;
				}
			}
		}
	}
}
