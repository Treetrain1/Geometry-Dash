package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.CheckpointSnapshot
import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.util.gdData
import me.treetrain1.geometrydash.util.gravity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
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

    override fun defineSynchedData() {
        this.entityData.define(CHECKPOINT_TYPE, CheckpointType.STANDARD)
    }

    override fun tick() {
        if (!this.level().isClientSide)
            this.checkpointTick()
    }

    protected open fun addCheckpoint(player: Player, gdData: GDData) {
        if (!gdData.playingGD) return
        val list = gdData.checkpoints
        if (list.map { it.entityId }.contains(this.id)) return
        list.add(CheckpointSnapshot(
            this.id,
            gdData.mode!!,
            CompoundTag().apply { gdData.gdModeData!!.save(this) },
            player.deltaMovement,
            player.yRot,
            gdData.scale,
            player.gravity,
            player.onGround(),
            gdData.isVisible,
            gdData.timeMod,
        ))
    }

    protected open fun checkpointTick() {
        val list: List<ServerPlayer> = this.level().getEntitiesOfClass(ServerPlayer::class.java, this.boundingBox)
        for (player in list) {
            if (player.isDeadOrDying) continue
            val gdData = player.gdData
            when (this.type) {
                CheckpointType.START -> gdData.enterGD()
                CheckpointType.END -> gdData.exitGD()
                else -> {}
            }
            if (this.type.shouldAddSpawn)
                this.addCheckpoint(player, gdData)
            gdData.markDirty()
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("Type", this.type.name)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        try {
            this.type = CheckpointType.valueOf(compound.getString("Type"))
        } catch (_: IllegalArgumentException) {
            this.type = CheckpointType.STANDARD
        }
    }

    enum class CheckpointType(val shouldAddSpawn: Boolean = true) {
        STANDARD,
        START,
        END(false);

        companion object {
            @JvmField
            val SERIALIZER: EntityDataSerializer<CheckpointType> = EntityDataSerializer.simpleEnum(CheckpointType::class.java)
        }
    }

    @Suppress("unused", "PropertyName")
    @PublishedApi
    internal val `access$entityData`: SynchedEntityData
        get() = entityData
}
