package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerDuck {

	@Unique
	private final GDData gdData = new GDData(Player.class.cast(this));

	@Unique
	@Override
	@NotNull
	public GDData geometryDash$getGDData() {
		return this.gdData;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void gd$tick(CallbackInfo ci) {
		this.gdData.tick();
	}

	@Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
	public void gd$isSwimming(CallbackInfoReturnable<Boolean> cir) {
		if (this.gdData.getPlayingGD()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
	public void gd$getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (this.gdData != null && this.gdData.getPlayingGD() && this.gdData.gdModeData != null) {
			cir.setReturnValue(this.gdData.gdModeData.getEntityDimensions());
		}
	}

	@Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
	public void gd$getStandingEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if (this.gdData != null && this.gdData.getPlayingGD() && this.gdData.gdModeData != null) {
			cir.setReturnValue(this.gdData.gdModeData.getEyeHeight());
		}
	}

	@Inject(method = "remove", at = @At("TAIL"))
	private void resetGD(Entity.RemovalReason reason, CallbackInfo ci) {
		// TODO: remove this at some point in favor of transferring gd data across player entities
		this.gdData.exitGD();
	}
}
