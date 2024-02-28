package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.util.getVec
import me.treetrain1.geometrydash.util.putVec
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3

/**
 * A snapshot of a player's GD data
 * <p>
 * Restored upon checkpoint respawn
 */
data class CheckpointSnapshot(
    val entityId: Int,
    val modeData: GDModeData?,
    val deltaMovement: Vec3,
    val yaw: Float,
    val scale: Float,
    val gravity: Vec3,
    val onGround: Boolean,
    val isVisible: Boolean = true,
    val timeMod: Float
) {

    companion object {
        @JvmField
        val CODEC: Codec<CheckpointSnapshot> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("entityId").forGetter(CheckpointSnapshot::entityId),
                GDModeData.CODEC.fieldOf("modeData").forGetter(CheckpointSnapshot::modeData),
                Vec3.CODEC.fieldOf("deltaMovement").forGetter(CheckpointSnapshot::deltaMovement),
                Codec.FLOAT.fieldOf("yaw").forGetter(CheckpointSnapshot::yaw),
                Codec.FLOAT.fieldOf("scale").forGetter(CheckpointSnapshot::scale),
                Vec3.CODEC.fieldOf("gravity").forGetter(CheckpointSnapshot::gravity),
                Codec.BOOL.fieldOf("onGround").forGetter(CheckpointSnapshot::onGround),
                Codec.BOOL.fieldOf("isVisible").forGetter(CheckpointSnapshot::isVisible),
                Codec.FLOAT.fieldOf("timeMod").forGetter(CheckpointSnapshot::timeMod)
            ).apply(instance, ::CheckpointSnapshot)
        }

        fun fromTag(compound: CompoundTag): CheckpointSnapshot
            = CheckpointSnapshot(
                compound.getInt("entityId"),
                compound.getGDModeData("modeData"),
                compound.getVec("deltaMovement"),
                compound.getFloat("yaw"),
                compound.getFloat("scale"),
                compound.getVec("gravity"),
                compound.getBoolean("onGround"),
                compound.getBoolean("isVisible"),
                compound.getFloat("timeMod"),
            )

        fun toBuf(buf: FriendlyByteBuf, snapshot: CheckpointSnapshot): FriendlyByteBuf {
            buf.writeVarInt(snapshot.entityId)
            buf.writeNullable(snapshot.modeData) { buf1, modeData -> buf1.writeNullable(modeData.toTag()) { buf2, tag -> buf2.writeNbt(tag) } }
            val delta = snapshot.deltaMovement
            buf.writeDouble(delta.x)
            buf.writeDouble(delta.y)
            buf.writeDouble(delta.z)
            buf.writeFloat(snapshot.yaw)
            buf.writeFloat(snapshot.scale)
            buf.writeVec3(snapshot.gravity)
            buf.writeBoolean(snapshot.onGround)
            buf.writeBoolean(snapshot.isVisible)
            buf.writeFloat(snapshot.timeMod)

            return buf
        }

        fun fromBuf(buf: FriendlyByteBuf): CheckpointSnapshot
            = CheckpointSnapshot(
                buf.readVarInt(),
                buf.readNullable { buf1 -> buf1.readNullable { buf2 -> buf2.readNbt() }?.toGDModeData() },
                buf.readVec3(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readVec3(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readFloat(),
            )
    }

    fun toTag(): CompoundTag {
        val compound = CompoundTag()
        compound.putInt("entityId", this.entityId)
        compound.putGDModeData("modeData", this.modeData)
        compound.putVec("deltaMovement", this.deltaMovement)
        compound.putFloat("yaw", this.yaw)
        compound.putFloat("size", this.scale)
        compound.putVec("gravity", this.gravity)
        compound.putBoolean("onGround", this.onGround)
        compound.putBoolean("isVisible", this.isVisible)
        compound.putFloat("timeMod", this.timeMod)

        return compound
    }
}
