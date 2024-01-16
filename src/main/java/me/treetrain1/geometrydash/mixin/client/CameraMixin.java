package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	protected abstract void setRotation(float yRot, float xRot);

	@Shadow
	public abstract float getYRot();

	@Shadow
	public abstract float getXRot();

	@Shadow
	protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

	@Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER), cancellable = true)
	private void setupGD(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
		if (!(entity instanceof PlayerDuck duck) || !duck.geometryDash$getGDData().getPlayingGD()) return;

		this.move(0, -1.137, -10);
		this.setRotation(this.getYRot() + 270.0F, 0F);

		ci.cancel();
	}
}
