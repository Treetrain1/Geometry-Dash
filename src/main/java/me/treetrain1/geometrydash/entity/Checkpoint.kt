package me.treetrain1.geometrydash.entity

import net.minecraft.world.entity.Entity

open class Checkpoint(
    type: EntityType<out Checkpoint>,
    level: Level,
) : Entity(type, level) {

    override fun aiStep() {
        super.aiStep()

        this.checkpointTick()
    }

    protected open fun checkpointTick() {
        val list: List<ServerPlayer> = this.level().getEntitiesOfClass(ServerPlayer::class.java, this.getBoundingBox())
        for (player in list) {
            val gdData = (player as PlayerDuck).geometryDash$getGDData()
            this.addCheckpoint(gdData)
        }
    }

    protected open fun addCheckpoint(gdData: GDData) {
        gdData.checkpoints.add(this.getStringUUID())
        gdData.updateCheckpoints()
    }
}