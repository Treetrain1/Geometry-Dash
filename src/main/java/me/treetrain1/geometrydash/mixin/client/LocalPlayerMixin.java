package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.data.mode.GDModeData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

	@Shadow
	public Input input;

	@Shadow
	private boolean crouching;

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void forceGDSprint(CallbackInfo ci) {
		if (((PlayerDuck) this).geometryDash$getGDData().getPlayingGD())
			((LocalPlayer) (Object) this).setSprinting(true);
	}

	@WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V"))
	private void gd$cancelSneak(Input instance, boolean isSneaking, float sneakingSpeedMultiplier, Operation<Void> original) {
		if (((PlayerDuck) this).geometryDash$getGDData().getPlayingGD()) {
			original.call(instance, false, 1F);
			this.crouching = false;
		} else {
			original.call(instance, isSneaking, sneakingSpeedMultiplier);
		}
	}

	@Inject(method = "hasEnoughFoodToStartSprinting", at = @At("HEAD"), cancellable = true)
	private void gd$hasEnoughFoodToStartSprinting(CallbackInfoReturnable<Boolean> cir) {
		if (((PlayerDuck) this).geometryDash$getGDData().getPlayingGD())
			cir.setReturnValue(true);
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V", shift = At.Shift.AFTER))
	private void gdInputTick(CallbackInfo ci) {
		GDData gdData = ((PlayerDuck) this).geometryDash$getGDData();
		GDModeData gdModeData = gdData.gdModeData;
		long window = Minecraft.getInstance().getWindow().getWindow();
		boolean jumping = InputConstants.isKeyDown(window, InputConstants.KEY_SPACE)
			|| InputConstants.isKeyDown(window, InputConstants.KEY_W)
			|| InputConstants.isKeyDown(window, InputConstants.KEY_UP)
			|| GLFW.glfwGetMouseButton(window, InputConstants.MOUSE_BUTTON_LEFT) == 1;
		String dashRingID = gdData.dashRingID;
		boolean isDashing = gdData.getIsDashing();
		gdData.inputBuffer = jumping;
		if (gdModeData != null) {
			// TODO: bounce from rings
			if (isDashing) {
				if (jumping) {
					LocalPlayer player = LocalPlayer.class.cast(this);
					player.setDeltaMovement(1.0, 1.0, 1.0); // TODO: use ring rotation
					return;
				}
				// returns before getting here, else not necessary
				gdMode.dashRingID = "";
			}
			if (!jumping) {
				gdData.ringLocked = false;
				if (gdModeData.unlockOnRelease())
					gdData.bufferLocked = false;
			}
			if (gdModeData.tickInput(this.input)) {
				gdData.ringLocked = true;
				if (gdModeData.lockOnSuccess()) gdData.bufferLocked = true;
			}
		}
	}
}
