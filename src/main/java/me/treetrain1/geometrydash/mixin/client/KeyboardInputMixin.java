package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "tick", at = @At("TAIL"))
	private void holdUpInGD(boolean isSneaking, float sneakingSpeedMultiplier, CallbackInfo ci) {
		KeyboardInput input = KeyboardInput.class.cast(this);
		if (((PlayerDuck) Minecraft.getInstance().player).geometryDash$getGDData().getPlayingGD()) {
			input.forwardImpulse = 1.0F;
			input.leftImpulse = 0.0F;
		}
	}
}
