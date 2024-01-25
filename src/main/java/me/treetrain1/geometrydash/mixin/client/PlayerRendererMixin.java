package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>  {

	public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void gd$init(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
		PlayerRenderer renderer = PlayerRenderer.class.cast(this);
		this.addLayer(new GDModeLayer(
			renderer,
			new CubePlayerModel<>(context.bakeLayer(GeometryDashClient.CUBE_PLAYER)),
			GDMode.CUBE, GDMode.CUBE_3D
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new ShipPlayerModel<>(context.bakeLayer(GeometryDashClient.SHIP_PLAYER)),
			GDMode.SHIP
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new BallPlayerModel<>(context.bakeLayer(GeometryDashClient.BALL_PLAYER)),
			GDMode.BALL
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new UFOPlayerModel<>(context.bakeLayer(GeometryDashClient.UFO_PLAYER)),
			GDMode.UFO
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new WavePlayerModel<>(context.bakeLayer(GeometryDashClient.WAVE_PLAYER)),
			GDMode.WAVE
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new RobotPlayerModel<>(context.bakeLayer(GeometryDashClient.ROBOT_PLAYER)),
			GDMode.ROBOT
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new SpiderPlayerModel<>(context.bakeLayer(GeometryDashClient.SPIDER_PLAYER)),
			GDMode.SPIDER
		));
		this.addLayer(new GDModeLayer(
			renderer,
			new SwingPlayerModel<>(context.bakeLayer(GeometryDashClient.SWING_PLAYER)),
			GDMode.SWING
		));
	}

	@Inject(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isSpectator()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void setGDProps(AbstractClientPlayer clientPlayer, CallbackInfo ci, PlayerModel<AbstractClientPlayer> playerModel) {
		if (((PlayerDuck) clientPlayer).geometryDash$getGDData().getPlayingGD()) {
			playerModel.setAllVisible(false);
			ci.cancel();
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
