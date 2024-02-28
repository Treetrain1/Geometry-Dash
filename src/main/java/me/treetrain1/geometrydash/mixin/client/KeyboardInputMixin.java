package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.data.mode.GDModeData;
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
		if (Minecraft.getInstance().player instanceof PlayerDuck duck
			&& duck.geometryDash$getGDData().getPlayingGD()
			&& duck.geometryDash$getGDData().getModeData() != null
		) {
			GDModeData gdModeData = duck.geometryDash$getGDData().getModeData();
			if (gdModeData.lockForwardsMovement()) {
				input.forwardImpulse = 1F;
			}
			if (!gdModeData.allowSidewaysMovement()) {
				input.leftImpulse = 0F;
			}
			input.shiftKeyDown = false;
		}
	}
}
