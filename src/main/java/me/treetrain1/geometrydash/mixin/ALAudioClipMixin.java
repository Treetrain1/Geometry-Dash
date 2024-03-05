package me.treetrain1.geometrydash.mixin;

import de.keksuccino.melody.resources.audio.openal.ALAudioClip;
import me.treetrain1.geometrydash.duck.GDClip;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ALAudioClip.class)
public class ALAudioClipMixin implements GDClip {

	@Final
	@Shadow
	protected int source;

	@Override
	public int getGeometryDash$source() {
		return this.source;
	}
}
