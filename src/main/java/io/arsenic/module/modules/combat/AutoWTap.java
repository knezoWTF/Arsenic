package io.arsenic.module.modules.combat;

import io.arsenic.event.events.HudEvent;
import io.arsenic.event.events.PacketSendEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.MinMaxSetting;
import io.arsenic.utils.TimerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public final class AutoWTap extends Module {
	private final MinMaxSetting delay = new MinMaxSetting("Delay", 0, 1000, 1,230, 270);
	private final BooleanSetting inAir = new BooleanSetting("In Air", false)
			.setDescription("Whether it should W tap in air");
	private final TimerUtils sprintTimer = new TimerUtils();
	private final TimerUtils tapTimer = new TimerUtils();
	private boolean holdingForward;
	private boolean sprinting;
	private int currentDelay;
	private boolean jumpedWhileHitting;

	public AutoWTap() {
		super("Auto WTap",
				"Automatically W Taps for you so the opponent takes more knockback",
				-1,
				Category.COMBAT);
		addSettings(delay, inAir);
	}

	@Override
	public void onEnable() {
		currentDelay = delay.getRandomValueInt();
		jumpedWhileHitting = false;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	private void onRenderHudEvent(HudEvent event) {
		if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_W) != 1) {
			sprinting = false;
			holdingForward = false;
			return;
		}

		if (!inAir.getValue() && !mc.player.isOnGround())
			return;

		if (mc.player.isOnGround()) {
			jumpedWhileHitting = false;
		}

		if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE) == 1 && !inAir.getValue()) {
			if (holdingForward || sprinting) {
				mc.options.forwardKey.setPressed(true);
				holdingForward = false;
				sprinting = false;
				return;
			}
		}

		if (holdingForward && tapTimer.delay(1)) {
			mc.options.forwardKey.setPressed(false);
			sprintTimer.reset();
			sprinting = true;
			holdingForward = false;
		}

		if (sprinting && sprintTimer.delay(currentDelay)) {
			mc.options.forwardKey.setPressed(true);
			sprinting = false;
			currentDelay = delay.getRandomValueInt();
		}
	}

	@EventHandler
	private void onPacketSendEvent(PacketSendEvent event) {
		if (!(event.packet instanceof PlayerInteractEntityC2SPacket packet))
			return;

		packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
			@Override
			public void interact(Hand hand) {
			}

			@Override
			public void interactAt(Hand hand, Vec3d pos) {
			}

			@Override
			public void attack() {
				if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_SPACE) == 1 && !inAir.getValue()) {
					jumpedWhileHitting = true;
				}

				if (!inAir.getValue() && !mc.player.isOnGround())
					return;

				if (!jumpedWhileHitting && mc.options.forwardKey.isPressed() && mc.player.isSprinting()) {
					sprintTimer.reset();
					holdingForward = true;
				}
			}
		});
	}
}