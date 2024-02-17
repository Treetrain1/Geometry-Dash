package me.treetrain1.geometrydash.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void flipCam(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		// TODO: Mirror everything properly, then use it after going through a mirror portal
		//poseStack.scale(-1, 1, 1);
		if (Minecraft.getInstance().player instanceof PlayerDuck duck) {
			GDData data = duck.geometryDash$getGDData();
			if (data.getPlayingGD())
				projectionMatrix.scale(data.cameraMirrorProgress, 1, 1);
		}
	}
}
