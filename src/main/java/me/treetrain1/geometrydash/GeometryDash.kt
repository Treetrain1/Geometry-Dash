package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.util.log
import net.fabricmc.api.ModInitializer
import kotlin.system.measureNanoTime

object GeometryDash : ModInitializer {

    override fun onInitialize() {
        val time = measureNanoTime {

        }

        log("Geometry Dash took $time nanoseconds")
    }
}
