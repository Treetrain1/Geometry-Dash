package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.data.GDData.MirrorDirection.Companion.getMirrorDirection
import me.treetrain1.geometrydash.data.GDData.MirrorDirection.Companion.putMirrorDirection
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.*
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

/**
 * A snapshot of a player's GD data
 * <p>
 * Restored upon checkpoint respawn
 * @param entityId The checkpoint entity's ID
 */
data class CheckpointSnapshot(
    @JvmField val entityId: Int,
    @JvmField val modeData: GDModeData?,
    @JvmField val deltaMovement: Vec3,
    @JvmField val xRot: Float,
    @JvmField val yRot: Float,
    @JvmField val scale: Float,
    @JvmField val gravityStrength: Double,
    @JvmField val gravityDirection: Direction,
    @JvmField val onGround: Boolean,
    @JvmField val cameraData: CameraData,
    @JvmField val isVisible: Boolean,
    @JvmField val timeMod: Float,
    @JvmField val dashOrbID: String,
    @JvmField val cameraMirrorProgress: Float,
    @JvmField val cameraMirrorDirection: GDData.MirrorDirection?
) {

    companion object {
        private const val ENTITY_ID_TAG = "entity_id"
        private const val MODE_DATA_TAG = "mode_data"
        private const val DELTA_MOVEMENT_TAG = "delta_movement"
        private const val XROT_TAG = "x_rot"
        private const val YROT_TAG = "y_rot"
        private const val SCALE_TAG = "scale"
        private const val GRAVITY_STRENGTH_TAG = "gravity_strength"
        private const val GRAVITY_DIRECTION_TAG = "gravity_direction"
        private const val ON_GROUND_TAG = "on_ground"
        private const val CAMERA_DATA_TAG = "camera_data"
        private const val IS_VISIBLE_TAG = "is_visible"
        private const val TIME_MULTIPLIER_TAG = "time_multiplier"
        private const val DASH_ORB_TAG = "dash_orb_id"
        private const val CAMERA_MIRROR_PROGRESS_TAG = "camera_mirror_progress"
        private const val CAMERA_MIRROR_DIRECTION_TAG = "camera_mirror_direction"

        @JvmField
        val CODEC: Codec<CheckpointSnapshot> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf(ENTITY_ID_TAG).forGetter(CheckpointSnapshot::entityId),
                GDModeData.CODEC.fieldOf(MODE_DATA_TAG).forGetter(CheckpointSnapshot::modeData),
                Vec3.CODEC.fieldOf(DELTA_MOVEMENT_TAG).forGetter(CheckpointSnapshot::deltaMovement),
                Codec.FLOAT.fieldOf(XROT_TAG).forGetter(CheckpointSnapshot::xRot),
                Codec.FLOAT.fieldOf(YROT_TAG).forGetter(CheckpointSnapshot::yRot),
                Codec.FLOAT.fieldOf(SCALE_TAG).forGetter(CheckpointSnapshot::scale),
                Codec.DOUBLE.fieldOf(GRAVITY_STRENGTH_TAG).forGetter(CheckpointSnapshot::gravityStrength),
                Direction.CODEC.fieldOf(GRAVITY_DIRECTION_TAG).forGetter(CheckpointSnapshot::gravityDirection),
                Codec.BOOL.fieldOf(ON_GROUND_TAG).forGetter(CheckpointSnapshot::onGround),
                CameraData.CODEC.fieldOf(CAMERA_DATA_TAG).forGetter(CheckpointSnapshot::cameraData),
                Codec.BOOL.fieldOf(IS_VISIBLE_TAG).forGetter(CheckpointSnapshot::isVisible),
                Codec.FLOAT.fieldOf(TIME_MULTIPLIER_TAG).forGetter(CheckpointSnapshot::timeMod),
                Codec.STRING.fieldOf(DASH_ORB_TAG).forGetter(CheckpointSnapshot::dashOrbID),
                Codec.FLOAT.fieldOf(CAMERA_MIRROR_PROGRESS_TAG).forGetter(CheckpointSnapshot::cameraMirrorProgress),
                GDData.MirrorDirection.CODEC.fieldOf(CAMERA_MIRROR_DIRECTION_TAG).forGetter(CheckpointSnapshot::cameraMirrorDirection),
            ).apply(instance, ::CheckpointSnapshot)
        }

        fun Player.restoreCheckpoint(data: GDData, entity: Checkpoint, checkpoint: CheckpointSnapshot) {
            data.modeData = checkpoint.modeData
            this.deltaMovement = checkpoint.deltaMovement
            this.xRot = checkpoint.xRot
            this.yRot = checkpoint.yRot
            data.scale = checkpoint.scale
            this.gravityStrength = checkpoint.gravityStrength
            this.gravityDirection = checkpoint.gravityDirection

            this.moveTo(entity.position())

            this.setOnGround(checkpoint.onGround)
            data.cameraData = checkpoint.cameraData
            data.isVisible = checkpoint.isVisible
            data.timeMod = checkpoint.timeMod
            data.dashOrbID = checkpoint.dashOrbID
            data.cameraMirrorProgress = checkpoint.cameraMirrorProgress
            data.cameraMirrorDirection = checkpoint.cameraMirrorDirection
        }

        fun fromTag(compound: CompoundTag): CheckpointSnapshot
            = CheckpointSnapshot(
                compound.getInt(ENTITY_ID_TAG),
                compound.getGDModeData(MODE_DATA_TAG),
                compound.getVec(DELTA_MOVEMENT_TAG),
                compound.getFloat(XROT_TAG),
                compound.getFloat(YROT_TAG),
                compound.getFloat(SCALE_TAG),
                compound.getDouble(GRAVITY_STRENGTH_TAG),
                Direction.byName(compound.getString(GRAVITY_DIRECTION_TAG)) ?: DEFAULT_GRAVITY_DIRECTION,
                compound.getBoolean(ON_GROUND_TAG),
                CameraData.fromTag(compound.getCompound(CAMERA_DATA_TAG)),
                compound.getBoolean(IS_VISIBLE_TAG),
                compound.getFloat(TIME_MULTIPLIER_TAG),
                compound.getString(DASH_ORB_TAG),
                compound.getFloat(CAMERA_MIRROR_PROGRESS_TAG),
                compound.getMirrorDirection(CAMERA_MIRROR_DIRECTION_TAG),
            )

        fun toBuf(buf: FriendlyByteBuf, snapshot: CheckpointSnapshot): FriendlyByteBuf {
            buf.writeVarInt(snapshot.entityId)
            buf.writeNullable(snapshot.modeData) { buf1, modeData -> buf1.writeNullable(modeData.toTag()) { buf2, tag -> buf2.writeNbt(tag) } }
            buf.writeVec3(snapshot.deltaMovement)
            buf.writeFloat(snapshot.xRot)
            buf.writeFloat(snapshot.yRot)
            buf.writeFloat(snapshot.scale)
            buf.writeDouble(snapshot.gravityStrength)
            buf.writeUtf(snapshot.gravityDirection.serializedName)
            buf.writeBoolean(snapshot.onGround)
            snapshot.cameraData.toBuf(buf)
            buf.writeBoolean(snapshot.isVisible)
            buf.writeFloat(snapshot.timeMod)
            buf.writeUtf(snapshot.dashOrbID)
            buf.writeFloat(snapshot.cameraMirrorProgress)
            buf.writeNullable(snapshot.cameraMirrorDirection) { buf1, dir -> buf1.writeUtf(dir.serializedName) }

            return buf
        }

        fun fromBuf(buf: FriendlyByteBuf): CheckpointSnapshot
            = CheckpointSnapshot(
                buf.readVarInt(),
                buf.readNullable { buf1 -> buf1.readNullable { buf2 -> buf2.readNbt() }?.toGDModeData() },
                buf.readVec3(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readDouble(),
                Direction.byName(buf.readUtf()) ?: DEFAULT_GRAVITY_DIRECTION,
                buf.readBoolean(),
                CameraData.fromBuf(buf),
                buf.readBoolean(),
                buf.readFloat(),
                buf.readUtf(),
                buf.readFloat(),
                buf.readNullable { buf1 -> GDData.MirrorDirection.CODEC.byName(buf1.readUtf() },
            )
    }

    fun toTag(): CompoundTag {
        val compound = CompoundTag()
        compound.putInt(ENTITY_ID_TAG, this.entityId)
        compound.putGDModeData(MODE_DATA_TAG, this.modeData)
        compound.putVec(DELTA_MOVEMENT_TAG, this.deltaMovement)
        compound.putFloat(XROT_TAG, this.xRot)
        compound.putFloat(YROT_TAG, this.yRot)
        compound.putFloat(SCALE_TAG, this.scale)
        compound.putDouble(GRAVITY_STRENGTH_TAG, this.gravityStrength)
        compound.putString(GRAVITY_DIRECTION_TAG, this.gravityDirection.serializedName)
        compound.putBoolean(ON_GROUND_TAG, this.onGround)
        compound.put(CAMERA_DATA_TAG, this.cameraData.toTag())
        compound.putBoolean(IS_VISIBLE_TAG, this.isVisible)
        compound.putFloat(TIME_MULTIPLIER_TAG, this.timeMod)
        compound.putString(DASH_ORB_TAG, this.dashOrbID)
        compound.putFloat(CAMERA_MIRROR_PROGRESS_TAG, this.cameraMirrorProgress)
        compound.putMirrorDirection(CAMERA_MIRROR_DIRECTION_TAG, this.cameraMirrorDirection)

        return compound
    }
}
