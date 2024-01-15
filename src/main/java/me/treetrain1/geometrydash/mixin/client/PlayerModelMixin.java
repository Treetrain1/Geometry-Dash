package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

	@Shadow
	@Final
	private List<ModelPart> parts;

	public PlayerModelMixin(ModelPart root) {
		super(root);
	}

	@Unique
	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.parts.forEach(ModelPart::resetPose);
		if (entity instanceof Player player && player instanceof PlayerDuck playerDuck && player == Minecraft.getInstance().player) {
			if (!playerDuck.geometryDash$getGDData().getPlayingGD()) {
				super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			} else {
				this.head.y -= 4F;
			}
		}
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (entity instanceof Player player && player instanceof PlayerDuck playerDuck && player == Minecraft.getInstance().player) {
			if (playerDuck.geometryDash$getGDData().getPlayingGD()) {
				ci.cancel();
				float tickDelta = ageInTicks - player.tickCount;
				this.head.xRot = playerDuck.geometryDash$getGDData().getJumpRotation(tickDelta) * Mth.DEG_TO_RAD;
			}
		}
	}
}
