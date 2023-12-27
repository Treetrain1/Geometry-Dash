package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	protected abstract void setPosition(double x, double y, double z);

	@Shadow
	protected abstract void setRotation(float yRot, float xRot);

	@Shadow
	public abstract float getYRot();

	@Shadow
	public abstract float getXRot();

	@Shadow
	protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

	@Shadow
	private float eyeHeightOld;

	@Shadow
	private float eyeHeight;

	@Inject(method = "setup", at = @At("TAIL"))
	private void setupGD(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
		if (!(entity instanceof PlayerDuck duck) || !duck.geometryDash$isGDMode()) return;

		this.setPosition(
			Mth.lerp(partialTick, entity.xo, entity.getX()),
			Mth.lerp(partialTick, entity.yo, entity.getY()) + (double)Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight),
			Mth.lerp(partialTick, entity.zo, entity.getZ())
		);
		this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));

		this.move(0, 0, -10);
		this.setRotation(this.getYRot() + 270.0F, this.getXRot());
	}
}
