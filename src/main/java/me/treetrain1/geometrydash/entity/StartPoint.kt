package me.treetrain1.geometrydash.entity

open class StartPoint(
    type: EntityType<out StartPoint>,
    level: Level
) : Checkpoint(type, level) {

    override protected fun addCheckpoint(gdData: GDData) {
        // TODO: better because this is terrible
        gdData.enterGD()
    }
}