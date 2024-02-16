package me.treetrain1.geometrydash.data

data class CameraData(
    @JvmField
    var pitch: Float,

    @JvmField
    var yaw: Float,

    @JvmField
    var roll: Float,

    @JvmField
    var playerOffset: Float,
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