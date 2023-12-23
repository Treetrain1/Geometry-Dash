package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
	private void preventGDFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
		if (this instanceof PlayerDuck duck && duck.geometryDash$isGDMode())
			ci.cancel();
	}
}
