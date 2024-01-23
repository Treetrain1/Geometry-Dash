package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.data.mode.GDModeData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

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
}
