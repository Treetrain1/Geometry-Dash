package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

open class EndPoint(
    type: EntityType<out EndPoint>,
    level: Level,
) : Checkpoint(type, level) {

    override fun addCheckpoint(gdData: GDData) {
        // TODO: Try to come up with a better idea for end positions
        gdData.exitGD()
    }
}
