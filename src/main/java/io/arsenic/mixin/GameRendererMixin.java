package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.event.events.GameRenderEvent;
import io.arsenic.module.modules.misc.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow public abstract Matrix4f getBasicProjectionMatrix(double fov);

	@Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

	@Shadow @Final private Camera camera;

	@Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
	private void onWorldRender(RenderTickCounter tickCounter, CallbackInfo ci) {
		double d = getFov(camera, tickCounter.getTickDelta(true), true);
		Matrix4f matrix4f = getBasicProjectionMatrix(d);
		MatrixStack matrixStack = new MatrixStack();
		Arsenic.EVENT_BUS.post(new GameRenderEvent(matrixStack,tickCounter.getTickDelta(true)));
	}

	@Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
		if (Arsenic.INSTANCE.getModuleManager().getModule(Freecam.class).isEnabled())
			cir.setReturnValue(false);
	}
}