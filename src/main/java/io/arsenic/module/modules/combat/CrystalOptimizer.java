package io.arsenic.module.modules.combat;

import io.arsenic.event.events.PacketSendEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.utils.WorldUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public final class CrystalOptimizer extends Module {
	public CrystalOptimizer() {
		super("Crystal Optimizer",
				"Makes your crystals disappear faster client-side so you can place crystals faster",
				-1,
				Category.COMBAT);
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
	private void onPacketSendEvent(PacketSendEvent event) {
		if (event.packet instanceof PlayerInteractEntityC2SPacket interactPacket) {
			interactPacket.handle(new PlayerInteractEntityC2SPacket.Handler() {
				@Override
				public void interact(Hand hand) {

				}

				@Override
				public void interactAt(Hand hand, Vec3d pos) {

				}

				@Override
				public void attack() {

					if (mc.crosshairTarget == null)
						return;

					if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY && mc.crosshairTarget instanceof EntityHitResult hit) {
						if (hit.getEntity() instanceof EndCrystalEntity) {
							StatusEffectInstance weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
							StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);
							if (!(weakness == null || strength != null && strength.getAmplifier() > weakness.getAmplifier() || WorldUtils.isTool(mc.player.getMainHandStack())))
								return;

							hit.getEntity().kill();
							hit.getEntity().setRemoved(Entity.RemovalReason.KILLED);
							hit.getEntity().onRemoved();
						}
					}
				}
			});
		}
	}
}
