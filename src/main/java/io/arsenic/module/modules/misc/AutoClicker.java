package io.arsenic.module.modules.misc;

import io.arsenic.event.events.TickEvent;
import io.arsenic.mixin.MinecraftClientAccessor;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.ModeSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.MathUtils;
import io.arsenic.utils.MouseSimulation;
import io.arsenic.utils.TimerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;


public final class AutoClicker extends Module {
	private final BooleanSetting onlyWeapon = new BooleanSetting("Only Weapon", true)
			.setDescription("Only left clicks with weapon in hand");
	private final BooleanSetting onlyBlocks = new BooleanSetting("Only Blocks", true)
			.setDescription("Only right clicks blocks");
	private final BooleanSetting onClick = new BooleanSetting("On Click", true);

	private final NumberSetting delay = new NumberSetting("Delay", 0, 1000, 0, 1);
	private final NumberSetting chance = new NumberSetting("Chance", 0, 100, 100, 1);
	private final ModeSetting<Mode> mode = new ModeSetting<>("Actions", Mode.All, Mode.class);
	private final TimerUtils timer = new TimerUtils();

	public enum Mode {
		All, Left, Right
	}

	public AutoClicker() {
		super("Auto Clicker",
				"Automatically clicks for you",
				-1,
				Category.MISC);

		addSettings(onlyWeapon, onClick, delay, chance, mode);
	}

	@Override
	public void onEnable() {
		timer.reset();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	//using this cuz its faster/instant
	@EventHandler
	private void onTickEvent(TickEvent event) {
		if (mc.player == null)
			return;

		if (mc.currentScreen != null)
			return;

		if (mc.crosshairTarget == null)
			return;

		if (timer.delay(delay.getValueFloat()) && chance.getValueInt() >= MathUtils.randomInt(1, 100)) {
			if (mode.isMode(Mode.Left)) {
				performLeftClick();
			}

			if (mode.isMode(Mode.Right)) {
				performRightClick();
			}

			if (mode.isMode(Mode.All)) {
				performLeftClick();
				performRightClick();
			}
		}
	}

	private void performRightClick() {
		Item mainhand = mc.player.getMainHandStack().getItem();
		Item offhand = mc.player.getOffHandStack().getItem();

		if (mainhand.getComponents().contains(DataComponentTypes.FOOD))
			return;

		if (offhand.getComponents().contains(DataComponentTypes.FOOD))
			return;

		if (mainhand instanceof RangedWeaponItem || offhand instanceof RangedWeaponItem)
			return;

		if (onClick.getValue() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS)
			return;

		MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

		((MinecraftClientAccessor) mc).invokeDoItemUse();
		timer.reset();
	}

	private void performLeftClick() {
		Item mainhand = mc.player.getMainHandStack().getItem();
		Item offhand = mc.player.getOffHandStack().getItem();

		if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
			return;

		if (mc.player.isUsingItem())
			return;

		if (onlyWeapon.getValue() && !(mainhand instanceof SwordItem || mainhand instanceof AxeItem))
			return;

		if (onClick.getValue() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
			return;

		MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_LEFT);

		((MinecraftClientAccessor) mc).invokeDoAttack();
		timer.reset();
	}
}
