package io.arsenic.module.modules.misc;

import io.arsenic.event.events.PacketReceiveEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.MinMaxSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;

public final class PingSpoof extends Module {
	private final MinMaxSetting ping = new MinMaxSetting("Ping", 0, 1000, 1, 0, 600)
			.setDescription("The ping you want to achieve");

	private int delay;
	public PingSpoof() {
		super("Ping Spoof",
				"Holds back packets making the server think your internet connection is bad.", -1, Category.MISC);
		addSettings(ping);
	}

	@Override
	public void onEnable() {
		delay = ping.getRandomValueInt();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	public void onPacketReceive(PacketReceiveEvent event) {
		if (event.packet instanceof KeepAliveS2CPacket packet) {
			new Thread(() -> {
				try {
					Thread.sleep(delay);
					mc.getNetworkHandler().getConnection().send(new KeepAliveC2SPacket(packet.getId()));
					delay = ping.getRandomValueInt();
				} catch (InterruptedException ignored) {}
			}).start();

			event.cancel();
		}
	}
}
