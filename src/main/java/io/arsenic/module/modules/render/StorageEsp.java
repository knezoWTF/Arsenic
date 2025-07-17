package io.arsenic.module.modules.render;

import io.arsenic.event.events.GameRenderEvent;
import io.arsenic.event.events.PacketReceiveEvent;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.NumberSetting;
import io.arsenic.utils.RenderUtils;
import io.arsenic.utils.WorldUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import java.awt.*;

public final class StorageEsp extends Module {
	private final NumberSetting alpha = new NumberSetting("Alpha", 1, 255, 125, 1);
	private final BooleanSetting donutBypass = new BooleanSetting("Donut Bypass", false);
	private final BooleanSetting tracers = new BooleanSetting("Tracers", false)
			.setDescription("Draws a line from your player to the storage block");

	public StorageEsp() {
		super("Storage ESP",
				"Renders storage blocks through walls",
				-1,
				Category.RENDER);
		addSettings(donutBypass, alpha, tracers);
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
	private void onGameRenderEvent(GameRenderEvent event) {
		renderStorages(event);
	}

	private Color getColor(BlockEntity blockEntity, int a) {
		if (blockEntity instanceof TrappedChestBlockEntity) {
			return new Color(200, 91, 0, a);
		} else if (blockEntity instanceof ChestBlockEntity) {
			return new Color(156, 91, 0, a);
		} else if (blockEntity instanceof EnderChestBlockEntity) {
			return new Color(117, 0, 255, a);
		} else if (blockEntity instanceof MobSpawnerBlockEntity) {
			return new Color(138, 126, 166, a);
		} else if (blockEntity instanceof ShulkerBoxBlockEntity) {
			return new Color(134, 0, 158, a);
		} else if (blockEntity instanceof FurnaceBlockEntity) {
			return new Color(125, 125, 125, a);
		} else if (blockEntity instanceof BarrelBlockEntity) {
			return new Color(255, 140, 140, a);
		} else if (blockEntity instanceof EnchantingTableBlockEntity) {
			return new Color(80, 80, 255, a);
		} else return new Color(255, 255, 255, 0);
	}

	private void renderStorages(GameRenderEvent event) {
		Camera cam = mc.gameRenderer.getCamera();
		if (cam != null) {
			MatrixStack matrices = event.matrices();
			matrices.push();
			Vec3d vec = cam.getPos();
			matrices.translate(-vec.x, -vec.y, -vec.z);
		}

		for (WorldChunk chunk : WorldUtils.getLoadedChunks().toList()) {
			for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
				BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);

				RenderUtils.renderFilledBox(event.matrices(), blockPos.getX() + 0.1F, blockPos.getY() + 0.05F, blockPos.getZ() + 0.1F, blockPos.getX() + 0.9F, blockPos.getY() + 0.85F, blockPos.getZ() + 0.9F, getColor(blockEntity, alpha.getValueInt()));

				if (tracers.getValue())
					RenderUtils.renderLine(event.matrices(), getColor(blockEntity, 255), mc.crosshairTarget.getPos(), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));
			}
		}

		MatrixStack matrixStack = event.matrices();
		matrixStack.pop();
	}

	@EventHandler
	private void onPacketReceiveEvent(PacketReceiveEvent event) {
		if (donutBypass.getValue()) {
			if (event.packet instanceof ChunkDeltaUpdateS2CPacket) {
				event.cancel();
			}
		}
	}
}
