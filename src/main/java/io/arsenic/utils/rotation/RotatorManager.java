package io.arsenic.utils.rotation;

import io.arsenic.Arsenic;
import io.arsenic.event.events.*;
import io.arsenic.utils.RotationUtils;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import static io.arsenic.Arsenic.EVENT_BUS;
import static io.arsenic.Arsenic.mc;


public final class RotatorManager {
	private boolean enabled;
	private boolean rotateBack;
	private boolean resetRotation;
	private Rotation currentRotation;
	private float clientYaw, clientPitch;
	private float serverYaw, serverPitch;

	public RotatorManager() {
		EVENT_BUS.subscribe(this);


		enabled = true;
		rotateBack = false;
		resetRotation = false;

		this.serverYaw = 0;
		this.serverPitch = 0;

		this.clientYaw = 0;
		this.clientPitch = 0;
	}

	public void shutDown() {
		EVENT_BUS.unsubscribe(this);
	}

	public Rotation getServerRotation() {
		return new Rotation(serverYaw, serverPitch);
	}

	public void enable() {
		enabled = true;
		rotateBack = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void disable() {
		if (isEnabled()) {
			enabled = false;
			if (!rotateBack) rotateBack = true;
		}
	}

	public void setRotation(Rotation rotation) {
		currentRotation = rotation;
	}

	public void setRotation(double yaw, double pitch) {
		setRotation(new Rotation(yaw, pitch));
	}

	private void resetClientRotation() {
		mc.player.setYaw(clientYaw);
		mc.player.setPitch(clientPitch);

		resetRotation = false;
	}

	public void setClientRotation(Rotation rotation) {
		this.clientYaw = mc.player.getYaw();
		this.clientPitch = mc.player.getPitch();

		mc.player.setYaw((float) rotation.yaw());
		mc.player.setPitch((float) rotation.pitch());

		resetRotation = true;
	}

	public void setServerRotation(Rotation rotation) {
		this.serverYaw = (float) rotation.yaw();
		this.serverPitch = (float) rotation.pitch();
	}

	private boolean wasDisabled;

	@EventHandler
	private void onAttackEvent(AttackEvent event) {
		if (!isEnabled() && wasDisabled) {
			enabled = true;
			wasDisabled = false;
		}
	}

	@EventHandler
	private void onItemUseEvent(ItemUseEvent event) {
		if (!event.isCancelled() && isEnabled()) {
			enabled = false;
			wasDisabled = true;
		}
	}

	@EventHandler
	private void onPacketSendEvent(PacketSendEvent event) {
		if (event.packet instanceof PlayerMoveC2SPacket packet) {
			serverYaw = packet.getYaw(serverYaw);
			serverPitch = packet.getPitch(serverPitch);
		}
	}

	@EventHandler
	private void onBlockBreakingEvent(BlockBreakingEvent event) {
		if (!event.isCancelled() && isEnabled()) {
			enabled = false;
			wasDisabled = true;
		}
	}

	@EventHandler
	private void onSendMovementPacketsEvent(MovementPacketEvent event) {
		if (isEnabled() && currentRotation != null) {
			setClientRotation(currentRotation);
			setServerRotation(currentRotation);

			return;
		}

		if (rotateBack) {
			Rotation serverRot = new Rotation(serverYaw, serverPitch);
			Rotation clientRot = new Rotation(mc.player.getYaw(), mc.player.getPitch());

			if (RotationUtils.getTotalDiff(serverRot, clientRot) > 1) {
				Rotation smoothRotation = RotationUtils.getSmoothRotation(serverRot, clientRot, 0.2);

				setClientRotation(smoothRotation);
				setServerRotation(smoothRotation);
			} else {
				rotateBack = false;
			}
		}
	}

	@EventHandler
	private void onPacketReceiveEvent(PacketReceiveEvent event) {
		if (event.packet instanceof PlayerPositionLookS2CPacket packet) {
			serverYaw = packet.getYaw();
			serverPitch = packet.getPitch();
		}
	}
}