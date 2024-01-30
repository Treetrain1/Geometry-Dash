package me.treetrain1.geometrydash.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@WrapOperation(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"))
	private void gravityJump(LivingEntity instance, double x, double y, double z, Operation<Void> original) {
		double jumpPower = y;
		Vec3 gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		Vec3 modified = gravity.normalize().multiply(x, y, z);
		original.call(instance, modified.x, modified.y, modified.z);
	}
}
