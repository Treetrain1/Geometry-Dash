package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.util.getVec
import me.treetrain1.geometrydash.util.putVec
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3

data class CameraData(
    @JvmField
    var pitch: Float = 0F,

    @JvmField
    var yaw: Float = 0F,

    @JvmField
    var roll: Float = 0F,

    @JvmField
    var playerOffset: Vec3 = Vec3.ZERO,
) {

    companion object {
        @JvmField
        val CODEC: Codec<CameraData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("pitch").forGetter(CameraData::pitch),
                Codec.FLOAT.fieldOf("yaw").forGetter(CameraData::yaw),
                Codec.FLOAT.fieldOf("roll").forGetter(CameraData::roll),
                Vec3.CODEC.fieldOf("player_offset").forGetter(CameraData::playerOffset)
            ).apply(instance, ::CameraData)
        }

        fun fromTag(compound: CompoundTag): CameraData
            = CameraData(
                compound.getFloat("pitch"),
                compound.getFloat("yaw"),
                compound.getFloat("roll"),
                compound.getVec("player_offset")
            )

        fun fromBuf(buf: FriendlyByteBuf): CameraData
            = CameraData(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readVec3(),
            )
    }

    fun toTag(): CompoundTag {
        val compound = CompoundTag()
        compound.putFloat("pitch", this.pitch)
        compound.putFloat("yaw", this.yaw)
        compound.putFloat("roll", this.roll)
        compound.putVec("player_offset", this.playerOffset)
        return compound
    }

    fun toBuf(buf: FriendlyByteBuf): FriendlyByteBuf {
        buf.writeFloat(this.pitch)
        buf.writeFloat(this.yaw)
        buf.writeFloat(this.roll)
        buf.writeVec3(this.playerOffset)
        return buf
    }
}
