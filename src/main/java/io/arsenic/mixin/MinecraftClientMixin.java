package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.event.events.*;
import io.arsenic.utils.MouseSimulation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientWorld world;

	@Shadow
	@Final
	private Window window;

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if (world != null) {
			Arsenic.EVENT_BUS.post(new TickEvent());
		}
	}

	@Inject(method = "onResolutionChanged", at = @At("HEAD"))
	private void onResolutionChanged(CallbackInfo ci) {
		Arsenic.EVENT_BUS.post(new ResolutionEvent(this.window));
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onItemUse(CallbackInfo ci) {
		ItemUseEvent event = new ItemUseEvent();

		Arsenic.EVENT_BUS.post(event);
		if (event.isCancelled()) ci.cancel();

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT, false);
			ci.cancel();
		}
	}

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onAttack(CallbackInfoReturnable<Boolean> cir) {
		AttackEvent event = new AttackEvent();

		Arsenic.EVENT_BUS.post(event);
		if (event.isCancelled()) cir.setReturnValue(false);

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_1, false);
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onBlockBreaking(boolean breaking, CallbackInfo ci) {
		BlockBreakingEvent event = new BlockBreakingEvent();

		Arsenic.EVENT_BUS.post(event);
		if (event.isCancelled()) ci.cancel();

		if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
			MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_1, false);
			ci.cancel();
		}
	}

	@Inject(method = "stop", at = @At("HEAD"))
	private void onClose(CallbackInfo ci) {
		Arsenic.INSTANCE.getProfileManager().saveProfile();
	}
}
