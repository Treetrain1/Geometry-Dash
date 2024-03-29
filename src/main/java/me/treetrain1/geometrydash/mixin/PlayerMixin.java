package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {

	@Unique
	private final GDData gdData = new GDData(Player.class.cast(this));

	@Unique
	@Override
	@NotNull
	public GDData geometryDash$getGDData() {
		return this.gdData;
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	protected void gd$defineSynchedData(CallbackInfo ci) {
		Player.class.cast(this).getEntityData().define(GDData.GD_DATA, new CompoundTag());
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void gd$tick(CallbackInfo ci) {
		if (this.gdData.getPlayingGD())
			this.gdData.tick();
	}

	@Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
	public void gd$isSwimming(CallbackInfoReturnable<Boolean> cir) {
		if (this.gdData.getPlayingGD()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
	public void gd$causeFoodExhaustion(float exhaustion, CallbackInfo ci) {
		if (this.gdData.getPlayingGD()) {
			ci.cancel();
		}
	}

	@Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
	public void gd$getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (this.gdData != null && this.gdData.getPlayingGD() && this.gdData.getModeData() != null) {
			cir.setReturnValue(this.gdData.getModeData().getEntityDimensions());
		}
	}

	@Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
	public void gd$getStandingEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if (this.gdData != null && this.gdData.getPlayingGD() && this.gdData.getModeData() != null) {
			cir.setReturnValue(this.gdData.getModeData().getEyeHeight());
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void addGDData(CompoundTag compound, CallbackInfo ci) {
		CompoundTag gdCompound = new CompoundTag();
		this.gdData.save(gdCompound);
		compound.put("GDData", gdCompound);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void readGDData(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains("GDData", Tag.TAG_COMPOUND)) {
			CompoundTag gdCompound = compound.getCompound("GDData");
			this.gdData.load(gdCompound);
			Player.class.cast(this).getEntityData().set(GDData.GD_DATA, gdCompound, true);
		}
	}

	@Override
	public void geometryDash$updateSyncedGDData() {
		Player.class.cast(this).getEntityData().set(GDData.GD_DATA, this.gdData.save(new CompoundTag()), true);
	}

	@Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
	private void cancelGDStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (this.gdData.getPlayingGD())
			ci.cancel();
	}
}
