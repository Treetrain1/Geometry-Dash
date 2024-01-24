package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.data.mode.CubeModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val title: String, val modeDataSupplier: () -> GDModeData) : StringRepresentable {
    CUBE("Cube", { CubeModeData() }),
    SHIP("Ship", { CubeModeData() }),
    BALL("Ball", { BallModeData() }),
    UFO("UFO", { UFOModeData() }),
    WAVE("Wave", { WaveModeData() }),
    ROBOT("Robot", { CubeModeData() }),
    SPIDER("Spider", { SpiderModeData() }),
    SWING("Swing", { CubeModeData() }),
    CUBE_3D("Cube 3D", { Cube3DModeData() });

    @Contract(pure = true)
    override fun toString(): String = this.name

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name
}
