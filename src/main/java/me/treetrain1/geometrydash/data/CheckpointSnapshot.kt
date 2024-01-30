package me.treetrain1.geometrydash.data

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
    val mode: GDMode,
    val modeData: CompoundTag,
    val deltaMovement: Vec3,
    val yaw: Float,
    val scale: Float,
    val gravity: Vec3,
    val onGround: Boolean,
    val isVisible: Boolean = true,
    val timeMod: Float
) {

    companion object {
        fun fromTag(compound: CompoundTag): CheckpointSnapshot
            = CheckpointSnapshot(
                compound.getInt("entityId"),
                try {
                    GDMode.valueOf(compound.getString("mode"))
                } catch (_: IllegalArgumentException) {
                    GDMode.CUBE
                },
                compound.getCompound("modeData"),
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
            buf.writeUtf(snapshot.mode.name)
            buf.writeNbt(snapshot.modeData)
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
                try {
                    GDMode.valueOf(buf.readUtf())
                } catch (_: IllegalArgumentException) {
                    GDMode.CUBE
                },
                buf.readNbt()!!,
                Vec3(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
                ),
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
        compound.put("modeData", this.modeData)
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
