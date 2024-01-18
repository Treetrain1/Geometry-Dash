package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	public abstract DamageSources damageSources();

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

	@Inject(method = "move", at = @At("TAIL"))
	private void gdCheck(MoverType type, Vec3 pos, CallbackInfo ci) {
		if (this instanceof PlayerDuck duck) {
			GDData data = duck.geometryDash$getGDData();
			if (data.getPlayingGD() && this.horizontalCollision) {
				LivingEntity.class.cast(this).die(this.damageSources().genericKill());
			}
		}
	}
}
