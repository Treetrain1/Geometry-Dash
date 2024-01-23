package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

// TODO: Replace with NBT
@Deprecated
open class StartPoint(
    type: EntityType<out StartPoint>,
    level: Level
) : Checkpoint(type, level) {

    override fun addCheckpoint(gdData: GDData) {
        // TODO: better because this is terrible
        gdData.enterGD()
    }
}
