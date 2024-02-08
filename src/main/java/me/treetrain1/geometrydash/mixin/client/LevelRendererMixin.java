package me.treetrain1.geometrydash.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

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
				projectionMatrix.scale(data.mirrorProgress, 1, 1);
		}
	}
}
