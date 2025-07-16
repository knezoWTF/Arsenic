package io.arsenic.event.events;

import io.arsenic.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public class PacketReceiveEvent extends CancellableEvent {
    public final Packet packet;

    public PacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }
}
