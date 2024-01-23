package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.duck.PlayerDuck
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
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
        this.checkpointTick()
    }

    protected open fun addCheckpoint(gdData: GDData) {
        val list = gdData.checkpoints
        if (list.contains(this.id)) return
        list.add(this.id)
    }

    protected open fun checkpointTick() {
        val list: List<ServerPlayer> = this.level().getEntitiesOfClass(ServerPlayer::class.java, this.boundingBox)
        for (player in list) {
            val gdData = (player as PlayerDuck).`geometryDash$getGDData`()
            if (this.type.shouldAddSpawn)
                this.addCheckpoint(gdData)
            when (this.type) {
                CheckpointType.START -> gdData.enterGD()
                CheckpointType.END -> gdData.exitGD()
                else -> {}
            }
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
