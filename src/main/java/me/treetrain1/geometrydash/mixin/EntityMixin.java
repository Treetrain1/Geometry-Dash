package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.GeometryDash;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.EntityDuck;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.network.C2SFailPacket;
import me.treetrain1.geometrydash.util.GDSharedConstantsKt;
import me.treetrain1.geometrydash.util.GDUtilsKt;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuck {

	@Unique
	private static final EntityDataAccessor<Double> GRAVITY_STRENGTH = SynchedEntityData.defineId(Entity.class, GeometryDash.DOUBLE_SERIALIZER);

	@Unique
	private static final EntityDataAccessor<Direction> GRAVITY_DIRECTION = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.DIRECTION);

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	@Final
	protected SynchedEntityData entityData;

	@Shadow
	public boolean minorHorizontalCollision;

	@Shadow
	public abstract boolean onGround();

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
			if (!player.isDeadOrDying() && data.getPlayingGD() && !data.getModeData().getWithstandsCollisions() && this.horizontalCollision && !this.minorHorizontalCollision) {
				player.setHealth(0);
				ClientPlayNetworking.send(new C2SFailPacket());
			}
		}
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setOnGroundWithKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER))
	private void instantGDInput(MoverType type, Vec3 pos, CallbackInfo ci) {
		if (this instanceof PlayerDuck duck) {
			var data = duck.geometryDash$getGDData();
			if (this.onGround()) {
				var mode = data.getModeData();
				if (mode != null)
					mode.tickInput();
			}
		}
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData()V", shift = At.Shift.AFTER))
	private void addGravityData(EntityType<?> entityType, Level level, CallbackInfo ci) {
		this.entityData.define(GRAVITY_STRENGTH, GDSharedConstantsKt.DEFAULT_GRAVITY_STRENGTH);
		this.entityData.define(GRAVITY_DIRECTION, GDSharedConstantsKt.DEFAULT_GRAVITY_DIRECTION);
	}

	@SuppressWarnings("ConstantValue")
	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void saveGravity(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
		double gravityStrength = this.entityData.get(GRAVITY_STRENGTH);
		Direction gravityDirection = this.entityData.get(GRAVITY_DIRECTION);
		if (gravityDirection == null) gravityDirection = Direction.DOWN;
		compound.putDouble("gravity_strength", gravityStrength);
		compound.putString("gravity_direction", gravityDirection.getSerializedName());
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	private void loadGravity(CompoundTag compound, CallbackInfo ci) {
		this.entityData.set(GRAVITY_STRENGTH, compound.getDouble("gravity_strength"));
		this.entityData.set(GRAVITY_DIRECTION, Direction.byName(compound.getString("gravity_direction")));
	}

	@Unique
	@Override
	public double geometryDash$getGravityStrength() {
		return this.entityData.get(GRAVITY_STRENGTH);
	}

	@Unique
	@Override
	public void geometryDash$setGravityStrength(double strength) {
		this.entityData.set(GRAVITY_STRENGTH, strength, true);
	}

	@Unique
	@Override
	@NotNull
	public Direction geometryDash$getGravityDirection() {
		return this.entityData.get(GRAVITY_DIRECTION);
	}

	@Unique
	@Override
	public void geometryDash$setGravityDirection(@NotNull Direction direction) {
		this.entityData.set(GRAVITY_DIRECTION, direction, true);
	}
}
