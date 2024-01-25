package me.treetrain1.geometrydash.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf

data class CheckpointSnapshot(
    val entityId: Int,
    val yaw: Float
) {

    companion object {
        fun fromTag(compound: CompoundTag): CheckpointSnapshot
            = CheckpointSnapshot(compound.getInt("EntityID"), compound.getFloat("yaw"))

        fun toBuf(buf: FriendlyByteBuf, snapshot: CheckpointSnapshot) {
            buf.writeVarInt(snapshot.entityId)
            buf.writeFloat(snapshot.yaw)
        }

        fun fromBuf(buf: FriendlyByteBuf): CheckpointSnapshot
            = CheckpointSnapshot(buf.readVarInt(), buf.readFloat())
    }

    fun toTag(): CompoundTag {
        val tag = CompoundTag()
        tag.putInt("EntityID", this.entityId)
        tag.putFloat("yaw", this.yaw)
        return tag
    }
}
