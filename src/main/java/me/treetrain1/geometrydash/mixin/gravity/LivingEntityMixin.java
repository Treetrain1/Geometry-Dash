package me.treetrain1.geometrydash.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
	private float gravityJump(float original) {
		double gravity = GravityAPI.calculateGravity(Entity.class.cast(this));
		return gravity < 0 ? original * -1 : original;
	}
}
