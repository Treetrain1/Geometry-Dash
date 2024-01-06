package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(CameraType.class)
public class CameraTypeMixin {

	@ModifyReturnValue(method = "isFirstPerson", at = @At("RETURN"))
	private boolean isGDMode(boolean original) {
		LocalPlayer player = Minecraft.getInstance().player;
		boolean notGD = player == null || !((PlayerDuck) player).geometryDash$getGDData().getPlayingGD();
		return original && notGD;
	}
}
