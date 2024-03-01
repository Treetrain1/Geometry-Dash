package me.treetrain1.geometrydash.registry

import net.minecraft.core.Holder.Reference
import net.minecraft.sounds.SoundEvent

object RegisterSounds {

    @JvmField
    val STEREO_MADNESS: Reference<SoundEvent> = register("music.stereo_madness")

    @JvmField
    val BACK_ON_TRACK: Reference<SoundEvent> = register("music.back_on_track")

    @JvmField
    val POLARGEIST: Reference<SoundEvent> = register("music.polargeist")

    @JvmField
    val DASH: Reference<SoundEvent> = register("music.dash")
}
