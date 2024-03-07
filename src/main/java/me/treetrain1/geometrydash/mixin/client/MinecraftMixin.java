package me.treetrain1.geometrydash.mixin.client;

import de.keksuccino.melody.resources.audio.openal.ALAudioClip;
import me.treetrain1.geometrydash.duck.MCDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MCDuck {

	@Shadow
	@Nullable
	public Screen screen;
	@Shadow
	@Nullable
	public ClientLevel level;
	@Unique
	@Nullable
	private ALAudioClip audioClip = null;

	@Inject(method = "runTick", at = @At("TAIL"))
	private void controlPause(boolean renderLevel, CallbackInfo ci) {
		var audioClip = this.audioClip;
		if (audioClip != null) {
			try {
				if (this.screen == null) {
					audioClip.resume();
				} else {
					if (this.level == null) {
						audioClip.stop();
						audioClip.closeQuietly();
						this.audioClip = null;
					} else audioClip.pause();
				}
			} catch (Exception ignored) {}
		}
	}

	@Nullable
	@Override
	public ALAudioClip getGeometryDash$audioClip() {
		return this.audioClip;
	}

	@Override
	public void setGeometryDash$audioClip(@Nullable ALAudioClip value) {
		var cur = this.audioClip;
		if (cur != null && cur != value && cur.isValidOpenAlSource()) {
			try {
				cur.stop();
			} catch (Exception ignored) {}
			cur.closeQuietly();
		}
		this.audioClip = value;
	}
}
