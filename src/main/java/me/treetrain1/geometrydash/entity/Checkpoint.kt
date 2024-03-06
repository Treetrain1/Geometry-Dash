package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.*
import me.treetrain1.geometrydash.util.gdData
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.gravityDirection
import me.treetrain1.geometrydash.util.gravityStrength
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

open class Checkpoint(
    type: EntityType<out Checkpoint>,
    level: Level,
) : StaticEntity(type, level) {

    companion object {
        @PublishedApi
        @JvmField
        internal val CHECKPOINT_TYPE: EntityDataAccessor<CheckpointType> = SynchedEntityData.defineId(Checkpoint::class.java, CheckpointType.SERIALIZER)
    }

    inline var type: CheckpointType
        get() = this.`access$entityData`[CHECKPOINT_TYPE]
        set(value) { this.`access$entityData`[CHECKPOINT_TYPE] = value }

    var song: SongSource? = null

    override fun defineSynchedData() {
        this.entityData.define(CHECKPOINT_TYPE, CheckpointType.STANDARD)
    }

    override fun tick() {
        if (!this.level().isClientSide)
            this.checkpointTick()
    }

    protected open fun addCheckpoint(player: Player, gdData: GDData) {
        val list = gdData.checkpoints
        if (list.map { it.entityId }.contains(this.id)) return
        list.add(CheckpointSnapshot(
            this.id,
            gdData.modeData,
            player.deltaMovement,
            player.xRot,
            player.yRot,
            gdData.scale,
            player.gravityStrength,
            player.gravityDirection,
            player.onGround(),
            gdData.cameraData,
            gdData.isVisible,
            gdData.timeMod,
            gdData.dashOrbID,
            gdData.cameraMirrorProgress,
            gdData.cameraMirrorDirection,
        ))
    }

    protected open fun checkpointTick() {
        val list: List<ServerPlayer> = this.level().getEntitiesOfClass(ServerPlayer::class.java, this.boundingBox)
        for (player in list) {
            if (player.isDeadOrDying) continue
            val gdData = player.gdData
            when (this.type) {
                CheckpointType.START -> {
                    gdData.enterGD(song = this.song)
                    player.xRot = this.xRot
                    player.yRot = this.yRot
                }
                CheckpointType.END -> gdData.exitGD()
                else -> {}
            }
            if (this.type.shouldAddSpawn)
                this.addCheckpoint(player, gdData)
            gdData.markDirty()
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("type", this.type.serializedName)
        if (this.type == CheckpointType.START)
            compound.putSongSource("song", this.song)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.type = CheckpointType.CODEC.byName(compound.getString("type")) ?: CheckpointType.STANDARD
        if (this.type == Checkpoint.START)
            this.song = compound.getSongSource("song")
    }

    enum class CheckpointType(val shouldAddSpawn: Boolean = true) : StringRepresentable {
        STANDARD,
        START,
        END(false);

        companion object {
            @JvmField
            val SERIALIZER: EntityDataSerializer<CheckpointType> = EntityDataSerializer.simpleEnum(CheckpointType::class.java)
        }

        override fun getSerializedName(): String = this.name.lowercase()
    }

    @Suppress("unused", "PropertyName")
    @PublishedApi
    internal val `access$entityData`: SynchedEntityData
        get() = entityData
}
