package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gravity_changer.api.GravityChangerAPI;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
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

	@SuppressWarnings({"DataFlowIssue", "InvalidInjectorMethodSignature"})
	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
	private Object setGDBob(Object original) {
		return (boolean) original && !((PlayerDuck) this.minecraft.player).geometryDash$getGDData().getPlayingGD();
	}

	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void cancelItemRenderGD(PoseStack poseStack, Camera activeRenderInfo, float partialTicks, CallbackInfo ci) {
		if (((PlayerDuck) this.minecraft.player).geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	public void frozenLib$shakeLevel(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo info) {
		Entity cameraEntity = this.mainCamera.getEntity();
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(cameraEntity);
        if (gravityDirection == Direction.UP) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F)); //Should be Roll/Z for flipping the level upside-down
            // If you just want to negate the rotation, head over to Gravity Changer's GameRendererMixin instead.
            // However, I can see why you wouldn't want to in case someone else were to port it...
            // You could just copy the mixin and apply a negative rotation compared to that in this one, I suppose.
        }
    }
}
