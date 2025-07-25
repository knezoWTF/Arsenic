package io.arsenic.module.modules.combat;

import io.arsenic.event.events.TickEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.InventoryUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class TotemOffhand extends Module {
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 0, 5, 0, 1);
    private final NumberSetting equipDelay = new NumberSetting("Equip Delay", 1, 5, 1, 1);
    private final BooleanSetting switchBack = new BooleanSetting("Switch Back", false);

    private int switchClock, equipClock, switchBackClock;
    private int previousSlot = -1;
    boolean sent, active = false;

    public TotemOffhand() {
        super("Totem Offhand", "Switches to your totem slot and offhands a totem if you dont have one already", -1, Category.COMBAT);
        addSettings(switchDelay, equipDelay, switchBack);
    }

    @Override
    public void onEnable() {
        reset();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onTickEvent(TickEvent event) {
        if(mc.currentScreen != null)
            return;

        if(mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING)
            active = true;

        if(active) {
            if (switchClock < switchDelay.getValueInt()) {
                switchClock++;
                return;
            }

            if(previousSlot == -1)
                previousSlot = mc.player.getInventory().selectedSlot;

            if (InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING)) {
                if (equipClock < equipDelay.getValueInt()) {
                    equipClock++;
                    return;
                }

                if (!sent) {
                    mc.getNetworkHandler().getConnection().send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                    sent = true;
                    return;
                }
            }

            if(switchBackClock < switchDelay.getValue()) {
                switchBackClock++;
            } else {
                if(switchBack.getValue())
                    InventoryUtils.setInvSlot(previousSlot);

                reset();
            }
        }
    }

    public void reset() {
        switchClock = 0;
        equipClock = 0;
        switchBackClock = 0;
        previousSlot = -1;

        sent = false;
        active = false;
    }
}