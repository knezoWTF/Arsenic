package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.event.events.PacketReceiveEvent;
import io.arsenic.event.events.PacketSendEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
	private static <T extends PacketListener> void onPacketReceive(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
		PacketReceiveEvent event = new PacketReceiveEvent(packet);

		Arsenic.EVENT_BUS.post(event);
		if (event.isCancelled()) ci.cancel();
	}

	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
	private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
		PacketSendEvent event = new PacketSendEvent(packet);

		Arsenic.EVENT_BUS.post(event);
		if (event.isCancelled()) ci.cancel();
	}
}
