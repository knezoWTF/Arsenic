package io.arsenic.module.modules.misc;

import io.arsenic.event.events.AttackEvent;
import io.arsenic.event.events.BlockBreakingEvent;
import io.arsenic.event.events.ItemUseEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.utils.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;


public final class Prevent extends Module {
	private final BooleanSetting doubleGlowstone = new BooleanSetting("Double Glowstone", false)
			.setDescription("Makes it so you can't charge the anchor again if it's already charged");
	private final BooleanSetting glowstoneMisplace = new BooleanSetting("Glowstone Misplace", false)
			.setDescription("Makes it so you can only right-click with glowstone only when aiming at an anchor");
	private final BooleanSetting anchorOnAnchor = new BooleanSetting("Anchor on Anchor", false)
			.setDescription("Makes it so you can't place an anchor on/next to another anchor unless charged");
	private final BooleanSetting obiPunch = new BooleanSetting("Obi Punch", false)
			.setDescription("Makes it so you can crystal faster by not letting you left click/start breaking the obsidian");
	private final BooleanSetting echestClick = new BooleanSetting("E-chest click", false)
			.setDescription("Makes it so you can't click on e-chests with PvP items");

	public Prevent() {
		super("Prevent",
				"Prevents you from certain actions",
				-1,
				Category.MISC);
		addSettings(doubleGlowstone, glowstoneMisplace, anchorOnAnchor, obiPunch, echestClick);
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
		if (mc.crosshairTarget instanceof BlockHitResult hit) {
			if (BlockUtils.isBlock(hit.getBlockPos(), Blocks.OBSIDIAN) && obiPunch.getValue() && mc.player.isHolding(Items.END_CRYSTAL))
				event.cancel();
		}
	}

	@EventHandler
	private void onBlockBreakingEvent(BlockBreakingEvent event) {
		if (mc.crosshairTarget instanceof BlockHitResult hit) {
			if (BlockUtils.isBlock(hit.getBlockPos(), Blocks.OBSIDIAN) && obiPunch.getValue() && mc.player.isHolding(Items.END_CRYSTAL))
				event.cancel();
		}
	}

	@EventHandler
	private void onItemUseEvent(ItemUseEvent event) {
		if (mc.crosshairTarget instanceof BlockHitResult hit) {
			if (BlockUtils.isAnchorCharged(hit.getBlockPos()) && doubleGlowstone.getValue() && mc.player.isHolding(Items.GLOWSTONE))
				event.cancel();

			if (!BlockUtils.isBlock(hit.getBlockPos(), Blocks.RESPAWN_ANCHOR) && glowstoneMisplace.getValue() && mc.player.isHolding(Items.GLOWSTONE))
				event.cancel();

			if (BlockUtils.isAnchorNotCharged(hit.getBlockPos()) && anchorOnAnchor.getValue() && mc.player.isHolding(Items.RESPAWN_ANCHOR))
				event.cancel();

			if (BlockUtils.isBlock(hit.getBlockPos(), Blocks.ENDER_CHEST) && echestClick.getValue() &&
					(mc.player.getMainHandStack().getItem() instanceof SwordItem
							|| mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL
							|| mc.player.getMainHandStack().getItem() == Items.OBSIDIAN
							|| mc.player.getMainHandStack().getItem() == Items.RESPAWN_ANCHOR
							|| mc.player.getMainHandStack().getItem() == Items.GLOWSTONE))
				event.cancel();
		}
	}
}
