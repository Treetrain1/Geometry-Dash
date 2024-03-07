package me.treetrain1.geometrydash.mixin.client;

import de.keksuccino.melody.resources.audio.openal.ALAudioClip;
import me.treetrain1.geometrydash.duck.MCDuck;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MCDuck {

	@Unique
	@Nullable
	private ALAudioClip audioClip = null;

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
