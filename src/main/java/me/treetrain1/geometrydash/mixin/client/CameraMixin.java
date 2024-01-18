package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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
	protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

	@WrapOperation(
		method = "setup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setRotation(FF)V",
			ordinal = 0
		)
	)
	private void gd$setupRotation(
		Camera instance, float yRot, float xRot, Operation<Void> original,
		BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick,
		@Share("gd$isGD") LocalBooleanRef isGD
	) {
		if (entity instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			yRot += 270F;
			xRot = 0;
			isGD.set(true);
		}

		original.call(instance, yRot, xRot);
	}

	@WrapOperation(
		method = "setup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			ordinal = 0
		)
	)
	private void gd$setupPosition(
		Camera instance, double x, double y, double z, Operation<Void> original,
		BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick,
		@Share("gd$isGD") LocalBooleanRef isGD
	) {
		original.call(instance, x, y, z);
		if (!isGD.get()) return;

		this.move(0D, -1.137D, -10D);
	}

	@Inject(
		method = "setup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			ordinal = 0,
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void gd$cancelThirdPerson(
		BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci,
		@Share("gd$isGD") LocalBooleanRef isGD
	) {
		if (isGD.get()) ci.cancel();
	}
}
