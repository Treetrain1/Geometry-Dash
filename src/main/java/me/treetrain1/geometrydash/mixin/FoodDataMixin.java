package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void preventHungerInGD(Player player, CallbackInfo ci) {
		if (((PlayerDuck) player).geometryDash$isGDMode()) {
			ci.cancel();
		}
	}
}
