package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.Cube3DModeData
import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.data.mode.CubeModeData
import me.treetrain1.geometrydash.data.mode.UFOModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val title: String, val modeDataSupplier: () -> GDModeData) : StringRepresentable {
    CUBE("Cube", { CubeModeData() }),
    SHIP("Ship", { CubeModeData() }),
    BALL("Ball", { CubeModeData() }),
    UFO("UFO", { UFOModeData() }),
    WAVE("Wave", { CubeModeData() }),
    ROBOT("Robot", { CubeModeData() }),
    SPIDER("Spider", { CubeModeData() }),
    SWING("Swing", { CubeModeData() }),
    CUBE_3D("Cube 3D", { Cube3DModeData() });

    @Contract(pure = true)
    override fun toString(): String = this.name

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name
}
