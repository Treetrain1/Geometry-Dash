package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.duck.PlayerDuck
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

open class Checkpoint(
    type: EntityType<out Checkpoint>,
    level: Level,
) : StaticEntity(type, level) {

    override fun tick() {
        this.checkpointTick()
    }

    protected open fun checkpointTick() {
        val list: List<ServerPlayer> = this.level().getEntitiesOfClass(ServerPlayer::class.java, this.boundingBox)
        for (player in list) {
            val gdData = (player as PlayerDuck).`geometryDash$getGDData`()
            this.addCheckpoint(gdData)
        }
    }

    protected open fun addCheckpoint(gdData: GDData) {
        val list = gdData.checkpoints
        if (list.contains(this.id)) return
        list.add(this.id)
    }
}
