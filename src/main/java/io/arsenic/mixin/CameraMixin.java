package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.event.events.CameraUpdateEvent;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class CameraMixin {
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
	private void update(Args args) {
		CameraUpdateEvent event = new CameraUpdateEvent(args.get(0), args.get(1), args.get(2));
		Arsenic.EVENT_BUS.post(event);

		args.set(0, event.getX());
		args.set(1, event.getY());
		args.set(2, event.getZ());
	}
}
