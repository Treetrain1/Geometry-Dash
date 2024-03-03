package me.treetrain1.geometrydash.worldgen

import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.world.phys.Vec3

class GeometrySpecialEffects : DimensionSpecialEffects(Float.NaN, false, SkyType.NONE, true, true) {
    override fun getBrightnessDependentFogColor(fogColor: Vec3, brightness: Float): Vec3 = fogColor

    override fun isFoggyAt(x: Int, y: Int): Boolean = false

}
