package me.treetrain1.geometrydash.entity

import net.minecraft.world.entity.Entity

open class Checkpoint(
    type: EntityType<out Checkpoint>,
    level: Level,
    val type; CheckpointType
) : Entity(type, level) {

    override fun aiStep() {
        super.aiStep()

        this.giveCheckpoints()
    }

    fun giveCheckpoints() {
        val list: List<Player> = this.level().getEntitiesOfClass(Player::class.java, this.getBoundingBox().inflate(0.08))
        for (player in list) {
            val gdData = (player as PlayerDuck).geometryDash$getGDData()
            gdData.checkpoints.add(this.getStringUUID())
            gdData.updateCheckpoints()
        }
    }

    enum class CheckpointType {
        START,
        MIDGAME,
        END
    }
}