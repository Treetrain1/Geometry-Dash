package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.data.mode.CubeModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val title: String, val isFlying: Boolean, val modeData: () -> GDModeData) : StringRepresentable {
    CUBE("Cube", false, { CubeModeData() }),
    SHIP("Ship", true, { ShipModeData() }),
    BALL("Ball", false, { BallModeData() }),
    UFO("UFO", true,  { UFOModeData() }),
    WAVE("Wave", true, { WaveModeData() }),
    ROBOT("Robot", false, { RobotModeData() }),
    SPIDER("Spider", false, { SpiderModeData() }),
    SWING("Swing", true, { SwingModeData() }),
    CUBE_3D("Cube 3D", false, { Cube3DModeData() });

    companion object {
        @JvmField
        val CODEC: Codec<GDMode> = StringRepresentable.fromEnum(::values)
    }

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name//.lowercase()
}
