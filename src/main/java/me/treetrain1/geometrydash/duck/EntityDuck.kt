package me.treetrain1.geometrydash.duck

import net.minecraft.world.phys.Vec3

@Suppress("FunctionName")
interface EntityDuck {

    fun `geometryDash$getGravity`(): Vec3

    fun `geometryDash$setGravity`(gravity: Vec3)
}
