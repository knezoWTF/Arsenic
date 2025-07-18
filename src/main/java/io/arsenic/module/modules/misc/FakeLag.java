package io.arsenic.module.modules.misc;

import com.google.common.collect.Queues;
import io.arsenic.event.events.PacketReceiveEvent;
import io.arsenic.event.events.PacketSendEvent;
import io.arsenic.event.events.PlayerTickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.MinMaxSetting;
import io.arsenic.utils.TimerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Queue;

public final class FakeLag extends Module {
	public final Queue<Packet<?>> packetQueue = Queues.newConcurrentLinkedQueue();
	public boolean bool;
	public Vec3d pos = Vec3d.ZERO;
	public TimerUtils timerUtil = new TimerUtils();
	private final MinMaxSetting lagDelay = new MinMaxSetting("Lag Delay", 0, 1000, 1, 100, 200);
	private final BooleanSetting cancelOnElytra = new BooleanSetting("Cancel on Elytra", false)
			.setDescription("Cancel the lagging effect when you're wearing an elytra");

	private int delay;
	public FakeLag() {
		super("Fake Lag",
				"Makes it impossible to aim at you by creating a lagging effect",
				-1,
				Category.MISC);
		addSettings(lagDelay, cancelOnElytra);
	}

	@Override
	public void onEnable() {
		timerUtil.reset();
		if (mc.player != null)
			pos = mc.player.getPos();

		delay = lagDelay.getRandomValueInt();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		reset();
		super.onDisable();
	}

	@EventHandler
	private void onPacketReceive(PacketReceiveEvent event) {
		if (mc.world == null)
			return;

		if(mc.player.isDead())
			return;

		if (event.packet instanceof ExplosionS2CPacket) {
			reset();
		}
	}

	@EventHandler
	private void onPacketSend(PacketSendEvent event) {
		if (mc.world == null || mc.player.isUsingItem() || mc.player.isDead())
			return;

		if (event.packet instanceof PlayerInteractEntityC2SPacket || event.packet instanceof HandSwingC2SPacket || event.packet instanceof PlayerInteractBlockC2SPacket || event.packet instanceof ClickSlotC2SPacket) {
			reset();
			return;
		}

		if (cancelOnElytra.getValue() && mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA) {
			reset();
			return;
		}

		if (!bool) {
			packetQueue.add(event.packet);
			event.cancel();
		}
	}

	@EventHandler
	private void onPlayerTickEvent(PlayerTickEvent event) {
		if (timerUtil.delay(delay)) {
			if (mc.player != null && !mc.player.isUsingItem()) {
				reset();
				delay = lagDelay.getRandomValueInt();
			}
		}
	}

	private void reset() {
		if (mc.player == null || mc.world == null)
			return;

		bool = true;

		synchronized (packetQueue) {
			while (!packetQueue.isEmpty()) {
				mc.getNetworkHandler().getConnection().send(packetQueue.poll(), null, false);
			}
		}

		bool = false;
		timerUtil.reset();
		pos = mc.player.getPos();
	}
}
