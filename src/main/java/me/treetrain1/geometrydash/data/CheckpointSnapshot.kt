package me.treetrain1.geometrydash.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf

/**
 * A snapshot of a player's GD data
 * <p>
 * Restored upon checkpoint respawn
 */
data class CheckpointSnapshot(
    val entityId: Int,
    val mode: GDMode,
    val modeData: CompoundTag,
    val deltaMovement: Vec3,
    val yaw: Float,
    val size: Float,
    val gravity: Double,
    val onGround: Boolean,
    val isVisible: Boolean = true,
    val timeMod: Float
) {

    companion object {
        fun fromTag(compound: CompoundTag): CheckpointSnapshot
            = CheckpointSnapshot(
                compound.getInt("entityId"),
                try {
                    GDMode.valueOf(compound.getUtf("mode"))
                } catch (_: IllegalArgumentException) {
                    GDMode.CUBE
                },
                compound.getCompound("modeData"),
                compound.getVec3("deltaMovement")
                compound.getFloat("yaw"),
                compound.getFloat("size"),
                compound.getDouble("gravity"),
                compound.getBoolean("onGround"),
                compound.getBoolean("isVisible"),
                compound.getFloat("timeMod"),
            )

        fun toBuf(buf: FriendlyByteBuf, snapshot: CheckpointSnapshot): FriendlyByteBuf {
            buf.writeVarInt(snapshot.entityId)
            buf.writeUtf(snapshot.mode.name)
            buf.writeCompound(snapshot.modeData)
            val delta = snapshot.deltaMovement
            buf.writeDouble(delta.x)
            buf.writeDouble(delta.y)
            buf.writeDouble(delta.z)
            buf.writeFloat(snapshot.yaw)
            buf.writeFloat(snapshot.size)
            buf.writeDouble(snapshot.gravity)
            buf.writeBoolean(snapshot.onGround)
            buf.writeBoolean(snapshot.isVisible)
            buf.writeFloat(snapshot.timeMod)

            return buf
        }

        fun fromBuf(buf: FriendlyByteBuf): CheckpointSnapshot
            = CheckpointSnapshot(
                buf.readVarInt(),
                try {
                    GDMode.valueOf(buf.readUtf())
                } catch (_: IllegalArgumentException) {
                    GDMode.CUBE
                },
                buf.readCompound(),
                Vec3(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
                ),
                buf.readFloat(),
                buf.readFloat(),
                buf.readDouble(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readFloat(),
            )
    }

    fun toTag(): CompoundTag {
        val compound = CompoundTag()
        compound.putInt("entityId", this.entityId)
        compound.putCompound("modeData", this.modeData)
        compound.putVec3("deltaMovement", this.deltaMovement)
        compound.putFloat("yaw", this.yaw)
        compound.putFloat("size", this.size)
        compound.putDouble("gravity", this.gravity)
        compound.putBoolean("onGround", this.onGround)
        compound.putBoolean("isVisible", this.isVisible)
        compound.putFloat("timeMod", this.timeMod)

        return tag
    }
}
