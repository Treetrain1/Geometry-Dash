package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

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
			GDModeData gdModeData = player.geometryDash$getGDData().gdModeData;
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
			Vec3 gravity = GDUtilsKt.getGravity(this);
			this.setDeltaMovement(this.getDeltaMovement().add(gravity.normalize().scale(GDSharedConstantsKt.GD_GRAVITY_PULL)));
		}
	}

	@WrapOperation(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;removeModifier(Ljava/util/UUID;)V"))
	private void removeGDSpeed(AttributeInstance instance, UUID identifier, Operation<Void> original) {
		original.call(instance, identifier);
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			original.call(instance, GDSharedConstantsKt.GD_MOVEMENT_SPEED.getId());
		}
	}

	@WrapOperation(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
	private void setGDSpeed(AttributeInstance instance, AttributeModifier modifier, Operation<Void> original) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			original.call(instance, GDSharedConstantsKt.GD_MOVEMENT_SPEED);
		} else {
			original.call(instance, modifier);
		}
	}
}
