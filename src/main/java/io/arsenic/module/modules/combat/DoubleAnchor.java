package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.utils.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public final class DoubleAnchor extends Module {
	public DoubleAnchor() {
		super("Double Anchor",
				"Helps you do the air place/double anchor",
				-1,
				Category.COMBAT);
	}

	private BlockPos pos;
	private int count;

	@Override
	public void onEnable() {
		pos = null;
		count = 0;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	private void onTickEvent(TickEvent event) {
		if (mc.currentScreen == null) {
			assert mc.player != null;
			if (mc.player.getMainHandStack().isOf(Items.RESPAWN_ANCHOR)) {
				assert mc.world != null;
				if (mc.crosshairTarget instanceof BlockHitResult h && BlockUtils.isAnchorCharged(h.getBlockPos())) {
					if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
						if (h.getBlockPos().equals(pos)) {
							if (count >= 1) return;
						} else {
							pos = h.getBlockPos();
							count = 0;
						}

						mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, h, 0));
						count++;
					}
				}
			}
		}
	}
}
