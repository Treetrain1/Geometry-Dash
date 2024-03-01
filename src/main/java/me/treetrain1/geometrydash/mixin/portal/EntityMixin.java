package me.treetrain1.geometrydash.mixin.portal;

import me.treetrain1.geometrydash.duck.PortalDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements PortalDuck {

	@Shadow
	private Vec3 position;

	@Shadow
	protected abstract ListTag newDoubleList(double... numbers);

	@Unique
	@Nullable
	private Vec3 overworldGDPortal = null;

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void saveGDPortal(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
		var overworldGDPortal = this.overworldGDPortal;
		if (overworldGDPortal != null)
			compound.put("OverworldGDPortal", this.newDoubleList(overworldGDPortal.x, overworldGDPortal.y, overworldGDPortal.z));
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void loadGDPortal(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains("OverworldGDPortal")) {
			ListTag portalTag = compound.getList("OverworldGDPortal", CompoundTag.TAG_DOUBLE);
			this.overworldGDPortal = new Vec3(portalTag.getDouble(0), portalTag.getDouble(1), portalTag.getDouble(2));
		}
	}

	@Unique
	@Nullable
	@Override
	public Vec3 geometryDash$getGDPortalPos() {
		return this.overworldGDPortal;
	}

	@Unique
	@Override
	public void geometryDash$setGDPortalPos() {
		this.overworldGDPortal = this.position;
	}
}
