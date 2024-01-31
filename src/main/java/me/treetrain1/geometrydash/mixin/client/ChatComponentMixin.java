package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	// TODO: fix
	@ModifyReturnValue(method = "isChatHidden", at = @At("RETURN"))
	private boolean gdHideChat(boolean original) {
		return original && !((PlayerDuck) this.minecraft.player).geometryDash$getGDData().getPlayingGD();
	}
}
