package io.arsenic.mixin;

import io.arsenic.Arsenic;
import io.arsenic.event.events.HudEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		HudEvent event = new HudEvent(context, tickCounter.getTickDelta(true));

		Arsenic.EVENT_BUS.post(event);
	}
}
