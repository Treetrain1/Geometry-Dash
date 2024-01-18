package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.Cube3DModeData
import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.data.mode.CubeModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val modeDataSupplier: () -> GDModeData) : StringRepresentable {
    CUBE({ CubeModeData() }),
    SHIP({ CubeModeData() }),
    BALL({ CubeModeData() }),
    UFO({ CubeModeData() }),
    WAVE({ CubeModeData() }),
    ROBOT({ CubeModeData() }),
    SPIDER({ CubeModeData() }),
    SWING({ CubeModeData() }),
    CUBE_3D({ Cube3DModeData() });

    @Contract(pure = true)
    override fun toString(): String = this.name

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name
}
