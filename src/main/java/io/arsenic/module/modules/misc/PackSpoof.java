package io.arsenic.module.modules.misc;

import io.arsenic.event.events.PacketReceiveEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class PackSpoof extends Module {
    public PackSpoof() {
        super("Pack Spoof", "Ignores custom resource packs", -1, Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onPacketReceiveEvent(PacketReceiveEvent event) {
        if(mc.getNetworkHandler() != null) {
            Packet<?> packet = event.packet;
            if (packet instanceof ResourcePackSendS2CPacket) {
                event.cancel();

                mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
                mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(mc.player.getUuid(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            }
        }
    }
}
