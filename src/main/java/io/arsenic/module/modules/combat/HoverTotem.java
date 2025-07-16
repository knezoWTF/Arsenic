package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.mixin.HandledScreenMixin;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public final class HoverTotem extends Module {
	private final NumberSetting delay = new NumberSetting("Delay", 0, 20, 0, 1);
	private final BooleanSetting hotbar = new BooleanSetting("Hotbar", true).setDescription("Puts a totem in your hotbar as well, if enabled (Setting below will work if this is enabled)");
	private final NumberSetting slot = new NumberSetting("Totem Slot", 1, 9, 1, 1)
			.setDescription("Your preferred totem slot");
	private final BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", false)
			.setDescription("Switches to totem slot when going inside the inventory");

	private int clock;

	public HoverTotem() {
		super("Hover Totem",
				"Equips a totem in your totem and offhand slots if a totem is hovered",
				-1,
				Category.COMBAT);
		addSettings(delay, hotbar, slot, autoSwitch);
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
		if (mc.currentScreen instanceof InventoryScreen inv) {
			Slot hoveredSlot = ((HandledScreenMixin) inv).getFocusedSlot();

			if (autoSwitch.getValue())
				mc.player.getInventory().selectedSlot = slot.getValueInt() - 1;

			if (hoveredSlot != null) {
				int slot = hoveredSlot.getIndex();

				if (slot > 35)
					return;

				int totem = this.slot.getValueInt() - 1;

				if (hoveredSlot.getStack().getItem() == Items.TOTEM_OF_UNDYING) {
					if (hotbar.getValue() && mc.player.getInventory().getStack(totem).getItem() != Items.TOTEM_OF_UNDYING) {
						if (clock > 0) {
							clock--;
							return;
						}

						mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, slot, totem, SlotActionType.SWAP, mc.player);
						clock = delay.getValueInt();
					} else if (!mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
						if (clock > 0) {
							clock--;
							return;
						}

						mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, mc.player);
						clock = delay.getValueInt();
					}
				}
			}
		} else clock = delay.getValueInt();
	}
}
