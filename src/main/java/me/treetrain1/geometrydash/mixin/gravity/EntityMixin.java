package me.treetrain1.geometrydash.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
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
		double gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		if (gravity < 0) {
			cir.setReturnValue(BlockPos.containing(this.position.add(Vec3.atLowerCornerOf(Direction.UP.getNormal()).scale(0.500001F))));
		}
	}

	@Inject(method = "getOnPosLegacy", at = @At("HEAD"), cancellable = true)
	private void onPosLegGravityMod(CallbackInfoReturnable<BlockPos> cir) {
		double gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		if (gravity < 0) {
			cir.setReturnValue(BlockPos.containing(new Vec3(0.0, 0.20000000298023224, 0.0).add(this.position)));
		}
	}

	@ModifyExpressionValue(method = "checkSupportingBlock", at = @At(value = "CONSTANT", args = {"doubleValue=1.0E-6"}))
	private double reverseGravitySupport(double original) {
		double gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		return gravity < 0 ? original * -1 : original;
	}

	@Inject(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z", shift = At.Shift.AFTER))
	private void reverseGravityModify(MoverType type, Vec3 pos, CallbackInfo ci) {
		if (!this.verticalCollisionBelow && this.verticalCollision) {
			double gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
			if (gravity < 0 && pos.y > 0.0) this.verticalCollisionBelow = true;
		}
	}
}
