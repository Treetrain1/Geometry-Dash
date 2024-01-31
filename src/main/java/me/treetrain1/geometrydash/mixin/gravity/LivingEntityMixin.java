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

	// TODO: fix slow movement
	@WrapOperation(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"))
	private void gravityJump(LivingEntity instance, double x, double y, double z, Operation<Void> original) {
		Vec3 gravity = GravityAPI.calculateGravity(Entity.class.cast(this)).normalize();
		double newX = gravity.x;
		double newZ = gravity.z;
		if (newX == 0) newX = 1; if (newZ == 0) newZ = 1;
		Vec3 modified = new Vec3(newX, gravity.y, newZ).multiply(x, y, z);
 		original.call(instance, modified.x, modified.y, modified.z);
	}
}
