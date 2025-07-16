package io.arsenic.module.modules.combat;

import io.arsenic.Arsenic;
import io.arsenic.event.events.HudEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AutoDoubleHand extends Module {
	private final BooleanSetting stopOnCrystal = new BooleanSetting("Stop On Crystal", false)
			.setDescription("Stops while Auto Crystal is running");
	private final BooleanSetting checkShield = new BooleanSetting("Check Shield", false)
			.setDescription("Checks if you're blocking with a shield");

	private final BooleanSetting onPop = new BooleanSetting("On Pop", false)
			.setDescription("Switches to a totem if you pop");
	private final BooleanSetting onHealth = new BooleanSetting("On Health", false)
			.setDescription("Switches to totem if low on health");
	private final BooleanSetting predict = new BooleanSetting("Predict Damage", true);

	private final NumberSetting health = new NumberSetting("Health", 1, 20, 2, 1)
			.setDescription("Health to trigger at");
	private final BooleanSetting onGround = new BooleanSetting("On Ground", true)
			.setDescription("Whether crystal damage is checked on ground or not");
	private final BooleanSetting checkPlayers = new BooleanSetting("Check Players", true)
			.setDescription("Checks for nearby players");

	private final NumberSetting distance = new NumberSetting("Distance", 1, 10, 5, 0.1)
			.setDescription("Player distance");
	private final BooleanSetting predictCrystals = new BooleanSetting("Predict Crystals", false);
	private final BooleanSetting checkAim = new BooleanSetting("Check Aim", false)
			.setDescription("Checks if the opponent is aiming at obsidian");
	private final BooleanSetting checkItems = new BooleanSetting("Check Items", false)
			.setDescription("Checks if the opponent is holding crystals");

	private final NumberSetting activatesAbove = new NumberSetting("Activates Above", 0, 4, 0.2, 0.1)
			.setDescription("Height to trigger at");

	private boolean belowHealth;
	private boolean offhandHasNoTotem;

	public AutoDoubleHand() {
		super("Auto Double Hand",
				"Automatically switches to your totem when you're about to pop",
				-1,
				Category.COMBAT);
		addSettings(stopOnCrystal, checkShield, onPop, onHealth, predict, health, onGround, checkPlayers, distance, predictCrystals, checkAim, checkItems, activatesAbove);
		belowHealth = false;
		offhandHasNoTotem = false;
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@SuppressWarnings("all")
	@EventHandler
	private void onRenderHudEvent(HudEvent event) {
		if (mc.player == null)
			return;

		if(Arsenic.INSTANCE.getModuleManager().getModule(AutoCrystal.class).crystalling && stopOnCrystal.getValue())
			return;

		double squaredDistance = distance.getValue() * distance.getValue();
		PlayerInventory inventory = mc.player.getInventory();

		if (checkShield.getValue() && mc.player.isBlocking())
			return;

		if (inventory.offHand.get(0).getItem() != Items.TOTEM_OF_UNDYING && onPop.getValue() && !offhandHasNoTotem) {
			offhandHasNoTotem = true;
			InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
		}

		if (inventory.offHand.get(0).getItem() == Items.TOTEM_OF_UNDYING)
			offhandHasNoTotem = false;

		if (mc.player.getHealth() <= health.getValue() && onHealth.getValue() && !belowHealth) {
			belowHealth = true;
			InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
		}

		if (mc.player.getHealth() > health.getValue())
			belowHealth = false;

		if(!predict.getValue())
			return;

		if (mc.player.getHealth() > 19)
			return;

		if (!onGround.getValue() && mc.player.isOnGround())
			return;

		if (checkPlayers.getValue() && mc.world.getPlayers().parallelStream().filter(e -> e != mc.player).noneMatch(p -> mc.player.squaredDistanceTo(p) <= squaredDistance))
			return;

		double above = activatesAbove.getValue();
		for (int floor = (int) Math.floor(above), i = 1; i <= floor; i++) {
			if (!mc.world.getBlockState(mc.player.getBlockPos().add(0, -i, 0)).isAir())
				return;
		}

		Vec3d playerPos = mc.player.getPos();
		BlockPos playerBlockPos = new BlockPos((int) playerPos.x, (int) playerPos.y - (int) above, (int) playerPos.z);
		if (!mc.world.getBlockState(new BlockPos(playerBlockPos)).isAir())
			return;

		List<EndCrystalEntity> crystals = nearbyCrystals();
		ArrayList<Vec3d> pos = new ArrayList<>();
		crystals.forEach(e -> pos.add(e.getPos()));
		if (predictCrystals.getValue()) {
			Stream<BlockPos> s = BlockUtils.getAllInBoxStream(mc.player.getBlockPos().add(-6, -8, -6), mc.player.getBlockPos().add(6, 2, 6))
					.filter(e -> mc.world.getBlockState(e).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(e).getBlock() == Blocks.BEDROCK)
					.filter(CrystalUtils::canPlaceCrystalClient);

			if (checkAim.getValue()) {

				if (checkItems.getValue())
					s = s.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
				else s = s.filter(this::arePeopleAimingAtBlock);
			}
			s.forEachOrdered(e -> pos.add(Vec3d.ofBottomCenter(e).add(0, 1, 0)));
		}

		for (Vec3d crys : pos) {
			double damage = DamageUtils.crystalDamage(mc.player, crys);

			if (damage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
				InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				break;
			}
		}
	}

	private List<EndCrystalEntity> nearbyCrystals() {
		Vec3d pos = mc.player.getPos();
		return mc.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6.0, -6.0, -6.0), pos.add(6.0, 6.0, 6.0)), e -> true);
	}

	private boolean arePeopleAimingAtBlock(final BlockPos block) {
		final Vec3d[] eyesPos = new Vec3d[1];
		final BlockHitResult[] hitResult = new BlockHitResult[1];

		return mc.world.getPlayers().parallelStream().filter(e -> e != mc.player).anyMatch(e -> {
			eyesPos[0] = RotationUtils.getEyesPos(e);
			hitResult[0] = mc.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
			return hitResult[0] != null && hitResult[0].getBlockPos().equals(block);
		});
	}

	private boolean arePeopleAimingAtBlockAndHoldingCrystals(final BlockPos block) {
		final Vec3d[] eyesPos = new Vec3d[1];
		final BlockHitResult[] hitResult = new BlockHitResult[1];

		return mc.world.getPlayers().parallelStream().filter(e -> e != mc.player).filter(e -> e.isHolding(Items.END_CRYSTAL)).anyMatch(e -> {
			eyesPos[0] = RotationUtils.getEyesPos(e);
			hitResult[0] = mc.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));

			return hitResult[0] != null && hitResult[0].getBlockPos().equals(block);
		});
	}
}
