package me.treetrain1.geometrydash.entity

open class EndPoint(
    type: EntityType<out EndPoint>,
    level: Level,
) : Checkpoint(type, level) {

    override protected fun checkpointTick() {
    }
}