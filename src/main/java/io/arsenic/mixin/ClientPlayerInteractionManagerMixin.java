package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.module.modules.misc.NoBreakDelay;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Shadow
	private int blockBreakingCooldown;

	@Redirect(method = "updateBlockBreakingProgress",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.GETFIELD, ordinal = 0))
	public int updateBlockBreakingProgress(ClientPlayerInteractionManager clientPlayerInteractionManager) {
		int cooldown = this.blockBreakingCooldown;
		return Arsenic.INSTANCE.getModuleManager().getModule(NoBreakDelay.class).isEnabled() ? 0 : cooldown;
	}
}
