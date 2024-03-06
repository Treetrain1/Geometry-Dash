package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.data.mode.GDModeData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.util.GDSharedConstantsKt;
import me.treetrain1.geometrydash.util.GDUtilsKt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	public abstract float getSpeed();

	@Shadow
	protected abstract float getFlyingSpeed();

	private LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
	private void gd$onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
		if (LivingEntity.class.cast(this) instanceof PlayerDuck player && GDData.GD_DATA.equals(key)) {
			player.geometryDash$getGDData().load((CompoundTag) LivingEntity.class.cast(this).getEntityData().get(key));
		}
	}

	@Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
	private void gd$canBreatheUnderwater(CallbackInfoReturnable<Boolean> cir) {
		if (LivingEntity.class.cast(this) instanceof PlayerDuck player && player.geometryDash$getGDData().getPlayingGD()) {
			GDModeData gdModeData = player.geometryDash$getGDData().getModeData();
			if (gdModeData != null) {
				cir.setReturnValue(gdModeData.preventDrowning());
			}
		}
	}

	@Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
	private void useGDLogic(CallbackInfo ci) {
		if (this instanceof PlayerDuck player && player.geometryDash$getGDData().getPlayingGD())
			ci.cancel();
	}

	@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isNoGravity()Z"))
	private boolean cancelGravityGD(boolean original) {
		return original || (this instanceof PlayerDuck player && player.geometryDash$getGDData().getPlayingGD());
	}

	@Inject(method = "travel", at = @At("TAIL"))
	private void gdGravity(Vec3 travelVector, CallbackInfo ci) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			LivingEntity livingEntity = LivingEntity.class.cast(this);
			Vec3 newPull = GDUtilsKt.toRelative(livingEntity, new Vec3(0.0, GDSharedConstantsKt.GD_GRAVITY_PULL, 0.0));
			this.addDeltaMovement(newPull);
		}
	}

	@WrapWithCondition(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
	private boolean setGDSpeed(AttributeInstance instance, AttributeModifier modifier) {
		return !(this instanceof PlayerDuck duck) || !duck.geometryDash$getGDData().getPlayingGD();
	}

	@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onGround()Z", ordinal = 2))
	private boolean fixGDFriction(boolean original) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD())
			return false;
		return original;
	}

	@ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At("RETURN"))
	private float fixGDFrictionSpeed(float original) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD())
			return this.getFlyingSpeed() * GDSharedConstantsKt.GD_MOVEMENT_SPEED;
		return original;
	}
}
