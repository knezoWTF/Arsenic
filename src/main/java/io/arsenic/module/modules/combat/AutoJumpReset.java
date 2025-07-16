package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.MathUtils;
import meteordevelopment.orbit.EventHandler;

public final class AutoJumpReset extends Module {
	private final NumberSetting chance = new NumberSetting("Chance", 0, 100, 100, 1);

	public AutoJumpReset() {
		super("Auto Jump Reset",
				"Automatically jumps for you when you get hit so you take less knockback (not good for crystal pvp)",
				-1,
				Category.COMBAT);
		addSettings(chance);
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
		if(MathUtils.randomInt(1, 100) <= chance.getValueInt()) {
			if (mc.currentScreen != null)
				return;

			if (mc.player.isUsingItem())
				return;

			if (mc.player.hurtTime == 0)
				return;

			if (mc.player.hurtTime == mc.player.maxHurtTime)
				return;

			if (!mc.player.isOnGround())
				return;

			if (mc.player.hurtTime == 9 && MathUtils.randomInt(1, 100) <= chance.getValueInt())
				mc.player.jump();
		}
	}
}
