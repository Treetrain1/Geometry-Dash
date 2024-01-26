package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

	@Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
	private void setGDFOV(CallbackInfoReturnable<Float> cir) {
		if (((PlayerDuck) this).geometryDash$getGDData().getPlayingGD()) {
			cir.setReturnValue(1F);
		}
	}
}
