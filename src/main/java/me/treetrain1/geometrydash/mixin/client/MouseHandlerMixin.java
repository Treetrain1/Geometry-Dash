package me.treetrain1.geometrydash.mixin.client;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
	private void preventTurningGD(CallbackInfo ci) {
		LocalPlayer player = this.minecraft.player;
		if (player != null && ((PlayerDuck) player).geometryDash$getGDData().isPlayingGD())
			ci.cancel();
	}
}
