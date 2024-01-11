package me.treetrain1.geometrydash.entity

open class StartPoint(
    type: EntityType<out StartPoint>,
    level: Level
) : Checkpoint(type, level) {

    override protected fun checkpointTick() {
    }
}