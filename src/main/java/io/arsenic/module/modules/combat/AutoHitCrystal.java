package io.arsenic.module.modules.combat;

import io.arsenic.Arsenic;
import io.arsenic.event.events.AttackListener;
import io.arsenic.event.events.ItemUseListener;
import io.arsenic.event.events.TickListener;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.KeybindSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.*;
import io.arsenic.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public final class AutoHitCrystal extends Module implements TickListener, ItemUseListener, AttackListener {
	private final KeybindSetting activateKey = new KeybindSetting("Activate Key", GLFW.GLFW_MOUSE_BUTTON_RIGHT, false)
			.setDescription("Key that does hit crystalling");
	private final BooleanSetting checkPlace = new BooleanSetting("Check Place", false)
			.setDescription("Checks if you can place the obsidian on that block");
	private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0, 20, 0, 1);
	private final NumberSetting switchChance = new NumberSetting("Switch Chance", 0, 100, 100, 1);
	private final NumberSetting placeDelay = new NumberSetting("Place Delay", 0, 20, 0, 1);
	private final NumberSetting placeChance = new NumberSetting("Place Chance", 0, 100, 100, 1).setDescription("Randomization");
	private final BooleanSetting workWithTotem = new BooleanSetting("Work With Totem", false);
	private final BooleanSetting workWithCrystal = new BooleanSetting("Work With Crystal", false);
	private final BooleanSetting clickSimulation = new BooleanSetting("Click Simulation", false)
			.setDescription("Makes the CPS hud think you're legit");
	private final BooleanSetting swordSwap = new BooleanSetting("Sword Swap", true);

	private int placeClock = 0;
	private int switchClock = 0;
	private boolean active;
	private boolean crystalling;
	private boolean crystalSelected;

	public AutoHitCrystal() {
		super("Auto Hit Crystal",
				"Automatically hit-crystals for you",
				-1,
				Category.COMBAT);
		addSettings(activateKey, checkPlace, switchDelay, switchChance, placeDelay, placeChance, workWithTotem, workWithCrystal, clickSimulation, swordSwap);
	}

	@Override
	public void onEnable() {
		eventManager.add(TickListener.class, this);
		eventManager.add(ItemUseListener.class, this);
		eventManager.add(AttackListener.class, this);
		reset();

		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(TickListener.class, this);
		eventManager.remove(ItemUseListener.class, this);
		eventManager.remove(AttackListener.class, this);
		super.onDisable();
	}

	@Override
	public void onTick() {
		int randomNum = MathUtils.randomInt(1, 100);

		if (mc.currentScreen != null)
			return;

		if (KeyUtils.isKeyPressed(activateKey.getKey())) {
			if(mc.crosshairTarget instanceof BlockHitResult hitResult && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
				if(!active && !BlockUtils.canPlaceBlockClient(hitResult.getBlockPos()) && checkPlace.getValue())
					return;

			ItemStack mainHandStack = mc.player.getMainHandStack();

			if (!(mainHandStack.getItem() instanceof SwordItem || (workWithTotem.getValue() && mainHandStack.isOf(Items.TOTEM_OF_UNDYING)) || workWithCrystal.getValue() && mainHandStack.isOf(Items.END_CRYSTAL)) && !active)
				return;
			else if(mc.crosshairTarget instanceof BlockHitResult hitResult && !active) {
				if(swordSwap.getValue()) {
					if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
						Block block = mc.world.getBlockState(hitResult.getBlockPos()).getBlock();

						crystalling = block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
					}
				}
			}

			active = true;

			if (!crystalling) {
				if (mc.crosshairTarget instanceof BlockHitResult hit) {
					if (hit.getType() == HitResult.Type.MISS)
						return;

					if (!BlockUtils.isBlock(hit.getBlockPos(), Blocks.OBSIDIAN)) {
						if(BlockUtils.isBlock(hit.getBlockPos(), Blocks.RESPAWN_ANCHOR) && BlockUtils.isAnchorCharged(hit.getBlockPos()))
							return;

						mc.options.useKey.setPressed(false);

						if (!mc.player.isHolding(Items.OBSIDIAN)) {
							if (switchClock > 0) {
								switchClock--;
								return;
							}

							if (randomNum <= switchChance.getValueInt()) {
								switchClock = switchDelay.getValueInt();
								InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);
							}
						}

						if (mc.player.isHolding(Items.OBSIDIAN)) {
							if (placeClock > 0) {
								placeClock--;
								return;
							}

							if (clickSimulation.getValue())
								MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

							randomNum = MathUtils.randomInt(1, 100);

							if (randomNum <= placeChance.getValueInt()) {
								WorldUtils.placeBlock(hit, true);

								placeClock = placeDelay.getValueInt();
								crystalling = true;
							}
						}
					}
				}
			}

			if (crystalling) {
				if (!mc.player.isHolding(Items.END_CRYSTAL) && !crystalSelected) {
					if (switchClock > 0) {
						switchClock--;
						return;
					}

					randomNum = MathUtils.randomInt(1, 100);

					if (randomNum <= switchChance.getValueInt()) {
						crystalSelected = InventoryUtils.selectItemFromHotbar(Items.END_CRYSTAL);
						switchClock = switchDelay.getValueInt();
					}
				}

				if (mc.player.isHolding(Items.END_CRYSTAL)) {
					AutoCrystal autoCrystal = Arsenic.INSTANCE.getModuleManager().getModule(AutoCrystal.class);

					if (!autoCrystal.isEnabled())
						autoCrystal.onTick();
				}
			}
		} else reset();
	}

	@Override
	public void onItemUse(ItemUseEvent event) {
		ItemStack mainHandStack = mc.player.getMainHandStack();
		if ((mainHandStack.isOf(Items.END_CRYSTAL) || mainHandStack.isOf(Items.OBSIDIAN)) && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS)
			event.cancel();
	}

	public void reset() {
		placeClock = placeDelay.getValueInt();
		switchClock = switchDelay.getValueInt();
		active = false;
		crystalling = false;
		crystalSelected = false;
	}

	@Override
	public void onAttack(AttackEvent event) {
		if (mc.player.getMainHandStack().isOf(Items.END_CRYSTAL) && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
			event.cancel();
	}
}
