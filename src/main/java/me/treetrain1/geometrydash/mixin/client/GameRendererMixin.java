package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private Camera mainCamera;

	@Shadow
	@Final
	Minecraft minecraft;

	@SuppressWarnings({"DataFlowIssue"})
	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
	private Object gd$removeBobbing(Object original) {
		return (boolean) original && !((PlayerDuck) this.minecraft.player).geometryDash$getGDData().getPlayingGD();
	}

	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void gd$stopItemRendering(PoseStack poseStack, Camera activeRenderInfo, float partialTicks, CallbackInfo ci) {
		if (((PlayerDuck) this.minecraft.player).geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}
}
