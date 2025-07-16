package io.arsenic.module.modules.combat;

import io.arsenic.event.events.AttackEvent;
import io.arsenic.event.events.BlockBreakingEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;

public final class NoMissDelay extends Module {
	private final BooleanSetting onlyWeapon = new BooleanSetting("Only weapon", true);
	private final BooleanSetting air = new BooleanSetting("Air", true)
			.setDescription("Whether to stop hits directed to the air");
	private final BooleanSetting blocks = new BooleanSetting("Blocks", false)
			.setDescription("Whether to stop hits directed to blocks");

	public NoMissDelay() {
		super("No Miss Delay",
				"Doesn't let you miss your sword/axe hits",
				-1,
				Category.COMBAT);
		addSettings(onlyWeapon, air, blocks);
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
	private void onAttackEvent(AttackEvent event) {
		if (onlyWeapon.getValue()
				&& !(mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getMainHandStack().getItem() instanceof AxeItem))
			return;

		switch (mc.crosshairTarget.getType()) {
			case MISS -> {
				if (air.getValue()) event.cancel();
			}
			case BLOCK -> {
				if (blocks.getValue()) event.cancel();
			}
		}
	}

	@EventHandler
	private void onBlockBreakingEvent(BlockBreakingEvent event) {
		if (onlyWeapon.getValue()
				&& !(mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getMainHandStack().getItem() instanceof AxeItem))
			return;

		if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			if (blocks.getValue()) event.cancel();
		}
	}
}
