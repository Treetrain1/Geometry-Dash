package me.treetrain1.geometrydash.entity

open class EndPoint(
    type: EntityType<out EndPoint>,
    level: Level,
) : Checkpoint(type, level) {

    override protected fun addCheckpoint(gdData: GDData) {
        // TODO: Try to come up with a better idea for end positions
        gdData.exitGD()
    }
}