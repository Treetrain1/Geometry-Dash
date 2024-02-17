package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class CameraData(
    @JvmField
    var pitch: Float = 0F,

    @JvmField
    var yaw: Float = 0F,

    @JvmField
    var roll: Float = 0F,

    @JvmField
    var playerOffset: Float = 0F,
) {

    companion object {
        @JvmField
        val CODEC: Codec<CameraData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("pitch").forGetter(CameraData::pitch),
                Codec.FLOAT.fieldOf("yaw").forGetter(CameraData::yaw),
                Codec.FLOAT.fieldOf("roll").forGetter(CameraData::roll),
                Codec.FLOAT.fieldOf("playerOffset").forGetter(CameraData::playerOffset)
            ).apply(instance, ::CameraData)
        }
    }
}
