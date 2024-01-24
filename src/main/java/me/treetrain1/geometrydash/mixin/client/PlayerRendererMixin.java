package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import me.treetrain1.geometrydash.GeometryDashClient;
import me.treetrain1.geometrydash.data.GDMode;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.entity.render.layer.GDModeLayer;
import me.treetrain1.geometrydash.entity.render.model.CubePlayerModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>  {

	public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void gd$init(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
		this.addLayer(new GDModeLayer(
			PlayerRenderer.class.cast(this),
			new CubePlayerModel<>(context.bakeLayer(GeometryDashClient.CUBE_PLAYER)),
			GDMode.CUBE, GDMode.CUBE_3D, GDMode.UFO, GDMode.BALL, GDMode.WAVE, GDMode.SPIDER
		));
	}

	@Inject(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isSpectator()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void setGDProps(AbstractClientPlayer clientPlayer, CallbackInfo ci, PlayerModel<AbstractClientPlayer> playerModel) {
		if (((PlayerDuck) clientPlayer).geometryDash$getGDData().getPlayingGD()) {
			playerModel.setAllVisible(false);
			ci.cancel();
		}
	}

	@Inject(method = "getRenderOffset(Lnet/minecraft/client/player/AbstractClientPlayer;F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
	private void gdRenderOffset(AbstractClientPlayer entity, float partialTicks, CallbackInfoReturnable<Vec3> cir) {
		if (((PlayerDuck) entity).geometryDash$getGDData().getPlayingGD()) {
			cir.setReturnValue(RotationUtil.vecPlayerToWorld(0.0, 0.0, 0.0, GravityChangerAPI.getGravityDirection(entity)));
		}
	}

	@WrapOperation(method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
	private void setGDScale(PoseStack instance, float x, float y, float z, Operation<Void> original, AbstractClientPlayer player) {
		if (((PlayerDuck) player).geometryDash$getGDData().getPlayingGD()) {
			original.call(instance, x * 2, y * 2, z * 2);
		} else {
			original.call(instance, x, y, z);
		}
	}
}
