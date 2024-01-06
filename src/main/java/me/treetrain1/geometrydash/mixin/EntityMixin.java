package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
	private void preventGDFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD())
			ci.cancel();
	}

	@WrapOperation(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void gdSprintParticles(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			original.call(instance, DustParticleOptions.REDSTONE, x, y, z, xSpeed, ySpeed, zSpeed);
		} else {
			original.call(instance, particleData, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
