package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.GeometryDash;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.EntityDuck;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.network.C2SFailPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuck {


	private static final EntityDataAccessor<Boolean> GRAVITY_NOT_NULL_DATA = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);

	private static final EntityDataAccessor<Double> GRAVITY_DATA = SynchedEntityData.defineId(Entity.class, GeometryDash.DOUBLE_SERIALIZER);

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	@Final
	protected SynchedEntityData entityData;

	@Shadow
	public boolean minorHorizontalCollision;

	@WrapWithCondition(
		method = "checkFallDamage",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;fallOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;F)V")
	)
	private boolean preventGDFallDamage(Block instance, Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        return !(this instanceof PlayerDuck duck) || !duck.geometryDash$getGDData().getPlayingGD();
    }

	@WrapOperation(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void gdSprintParticles(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
		ParticleOptions particleOptions = (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) ? DustParticleOptions.REDSTONE : particleData;
		original.call(instance, particleOptions, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	@Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void gdCheck(CallbackInfo ci) {
		Entity entity = Entity.class.cast(this);
		if (entity.level().isClientSide && this instanceof PlayerDuck duck && entity instanceof Player player) {
			GDData data = duck.geometryDash$getGDData();
			if (!player.isDeadOrDying() && data.getPlayingGD() && this.horizontalCollision && !this.minorHorizontalCollision) {
				player.setHealth(0);
				ClientPlayNetworking.send(new C2SFailPacket());
			}
		}
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData()V"))
	private void addGravityData(EntityType<?> entityType, Level level, CallbackInfo ci) {
		this.entityData.define(GRAVITY_NOT_NULL_DATA, false);
		this.entityData.define(GRAVITY_DATA, 1.0);
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void saveGravity(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
		double gravity = this.entityData.get(GRAVITY_DATA);
		boolean notNull = this.entityData.get(GRAVITY_NOT_NULL_DATA);
		compound.putBoolean("GravityNotNull", notNull);
		compound.putDouble("Gravity", gravity);
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void loadGravity(CompoundTag compound, CallbackInfo ci) {
		this.entityData.set(GRAVITY_NOT_NULL_DATA, compound.getBoolean("GravityNotNull"));
		this.entityData.set(GRAVITY_DATA, compound.getDouble("Gravity"));
	}

	@Override
	@Nullable
	public Double geometryDash$getGravity() {
		boolean notNull = this.entityData.get(GRAVITY_NOT_NULL_DATA);
		return notNull ? this.entityData.get(GRAVITY_DATA) : null;
	}

	@Override
	public void geometryDash$setGravity(Double gravity) {
		if (gravity == null) {
			this.entityData.set(GRAVITY_NOT_NULL_DATA, false);
		} else {
			this.entityData.set(GRAVITY_DATA, gravity);
			this.entityData.set(GRAVITY_NOT_NULL_DATA, true);
		}
	}
}
