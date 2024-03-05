package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.data.GDMode

class Cube3DModeData(
    targetCubeRot: Float = 0F,
    cubeRot: Float = 0F,
    prevCubeRot: Float = 0F,
) : CubeModeData(targetCubeRot, cubeRot, prevCubeRot) {

    override val mode: GDMode = GDMode.CUBE_3D

    companion object {
        @JvmField
        val CODEC: Codec<Cube3DModeData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("target_rot").forGetter(Cube3DModeData::targetCubeRot),
                Codec.FLOAT.fieldOf("rot").forGetter(Cube3DModeData::cubeRot),
                Codec.FLOAT.fieldOf("prev_rot").forGetter(Cube3DModeData::prevCubeRot)
            ).apply(instance, ::Cube3DModeData)
        }
    }

    override val withstandsCollisions: Boolean = true

    override fun useGDCamera(): Boolean {
        return false
    }

    override fun lockCamera(): Boolean {
        return false
    }

    override val codec: Codec<out GDModeData> = CODEC
}
