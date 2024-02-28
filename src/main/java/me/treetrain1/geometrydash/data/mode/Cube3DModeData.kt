package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.GDMode

class Cube3DModeData : CubeModeData() {

    override val mode: GDMode = GDMode.CUBE_3D

    companion object {
        @JvmField
        val CODEC: Codec<Cube3DModeData> = Codec.unit(::Cube3DModeData)
    }

    override fun useGDCamera(): Boolean {
        return false
    }

    override fun lockCamera(): Boolean {
        return false
    }

    override val codec: Codec<out GDModeData> = CODEC
}
