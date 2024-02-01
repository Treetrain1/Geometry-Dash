package me.treetrain1.geometrydash.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

	@Shadow
	public boolean verticalCollisionBelow;

	@Shadow
	public boolean verticalCollision;

	@Shadow
	private Vec3 position;

	@Inject(method = "getBlockPosBelowThatAffectsMyMovement", at = @At("HEAD"), cancellable = true)
	private void onPosGravityMod(CallbackInfoReturnable<BlockPos> cir) {
		Vec3 offset = GDUtilsKt.toRelative(Entity.class.cast(this), new Vec3(0, 0.500001F, 0));
		cir.setReturnValue(BlockPos.containing(this.position.add(offset)));
	}

	@Inject(method = "getOnPosLegacy", at = @At("HEAD"), cancellable = true)
	private void onPosLegGravityMod(CallbackInfoReturnable<BlockPos> cir) {
		Vec3 offset = GDUtilsKt.toRelative(Entity.class.cast(this), new Vec3(0, -0.20000000298023224, 0));
		cir.setReturnValue(BlockPos.containing(this.position.add(offset)));
	}

	// TODO: test this specifically
	@WrapOperation(method = "checkSupportingBlock", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;", ordinal = 0))
	private AABB boundingBoxGravMod(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Operation<AABB> original) {
		double minOffset = 1.0E-6;
		Vec3 offset = GDUtilsKt.toRelative(Entity.class.cast(this), new Vec3(0, minOffset, 0));
		double xOffset = offset.x;
		double yOffset = offset.y + minOffset;
		double zOffset = offset.z;

        return original.call(minX - xOffset, minY - yOffset, minZ - zOffset, maxX, maxY, maxZ);
    }

	@Inject(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z", shift = At.Shift.AFTER))
	private void reverseGravityModify(MoverType type, Vec3 pos, CallbackInfo ci) {
		if (!this.verticalCollisionBelow && this.verticalCollision) {
			Vec3 gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
			if (
				(gravity.y < 0 && pos.y > 0)
				|| (gravity.x > 0 && pos.x < 0)
				|| (gravity.x < 0 && pos.x > 0)
				|| (gravity.z > 0 && pos.z < 0)
				|| (gravity.z < 0 && pos.z > 0)
			) this.verticalCollisionBelow = true;
		}
	}
}
