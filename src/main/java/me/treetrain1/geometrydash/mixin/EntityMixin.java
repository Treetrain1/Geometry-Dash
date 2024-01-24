package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.network.C2SKillPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	public abstract DamageSources damageSources();

	@Shadow
	public abstract boolean isAlive();

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
			if (!player.isDeadOrDying() && data.getPlayingGD() && this.horizontalCollision) {
				player.setHealth(0);
				ClientPlayNetworking.send(new C2SKillPacket());
			}
		}
	}
}
