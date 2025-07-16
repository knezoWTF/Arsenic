package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public class PacketSendEvent extends CancellableEvent {
    public final Packet packet;

    public PacketSendEvent(Packet packet) {
        this.packet = packet;
    }
}
