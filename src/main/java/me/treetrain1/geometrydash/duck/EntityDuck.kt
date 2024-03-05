package me.treetrain1.geometrydash.duck

import net.minecraft.core.Direction

@Suppress("FunctionName")
interface EntityDuck {

    fun `geometryDash$getGravityStrength`(): Double

    fun `geometryDash$setGravityStrength`(strength: Double)

    fun `geometryDash$getGravityDirection`(): Direction

    fun `geometryDash$setGravityDirection`(direction: Direction)
}
