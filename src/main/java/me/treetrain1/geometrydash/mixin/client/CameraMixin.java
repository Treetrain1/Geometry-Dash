package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
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

	@Shadow
	public abstract void setRotation(float yRot, float xRot);

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
		@Share("gd$useGDCamera") LocalBooleanRef useGDCamera, @Share("gd$yRot") LocalFloatRef gdYRot
	) {
		if (entity instanceof PlayerDuck duck
			&& duck.geometryDash$getGDData().getPlayingGD()
			&& duck.geometryDash$getGDData().modeData != null
			&& duck.geometryDash$getGDData().modeData.useGDCamera()
		) {
			gdYRot.set(yRot);
			xRot = 0;
			useGDCamera.set(true);
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
		@Share("gd$useGDCamera") LocalBooleanRef useGDCamera, @Share("gd$yRot") LocalFloatRef gdYRot
	) {
		original.call(instance, x, y, z);
		if (!useGDCamera.get()) return;

		this.move(0D, 0D, -10D);
		this.setRotation(gdYRot.get() + 270F, 0F);
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
		@Share("gd$useGDCamera") LocalBooleanRef useGDCamera
	) {
		if (useGDCamera.get()) ci.cancel();
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F"
		)
	)
	public float gd$tick(Entity instance, Operation<Float> original) {
		float value = original.call(instance);
		if (instance instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD() && duck.geometryDash$getGDData().modeData != null) {
			value += duck.geometryDash$getGDData().modeData.getCameraYOffset();
		}
		return value;
	}
}
