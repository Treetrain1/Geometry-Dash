package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.data.mode.CubeModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val title: String, val modeData: () -> GDModeData, val isFlying: Boolean = false) : StringRepresentable {
    CUBE("Cube", { CubeModeData() }),
    SHIP("Ship", { ShipModeData() }, isFlying = true),
    BALL("Ball", { BallModeData() }),
    UFO("UFO", { UFOModeData() }, isFlying = true),
    WAVE("Wave", { WaveModeData() }, isFlying = true),
    ROBOT("Robot", { RobotModeData() }),
    SPIDER("Spider", { SpiderModeData() }),
    SWING("Swing", { SwingModeData() }, isFlying = true),
    CUBE_3D("Cube 3D", { Cube3DModeData() });

    companion object {
        @JvmField
        val CODEC: Codec<GDMode> = StringRepresentable.fromEnum(::values)
    }

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name.lowercase()
}
