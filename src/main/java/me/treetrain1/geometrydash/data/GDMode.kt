package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.data.mode.CubeModeData
import me.treetrain1.geometrydash.registry.GDRegistries
import me.treetrain1.geometrydash.registry.register
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract

enum class GDMode(val title: String, val modeData: () -> GDModeData, private val codec: Codec<out GDModeData>, val isFlying: Boolean = false) : StringRepresentable {
    CUBE("Cube", { CubeModeData() }, CubeModeData.CODEC),
    SHIP("Ship", { ShipModeData() }, ShipModeData.CODEC, isFlying = true),
    BALL("Ball", { BallModeData() }, BallModeData.CODEC),
    UFO("UFO", { UFOModeData() }, UFOModeData.CODEC, isFlying = true),
    WAVE("Wave", { WaveModeData() }, WaveModeData.CODEC, isFlying = true),
    ROBOT("Robot", { RobotModeData() }, RobotModeData.CODEC),
    SPIDER("Spider", { SpiderModeData() }, SpiderModeData.CODEC),
    SWING("Swing", { SwingModeData() }, SwingModeData.CODEC, isFlying = true),
    CUBE_3D("Cube 3D", { Cube3DModeData() }, Cube3DModeData.CODEC);

    companion object {
        @JvmField
        val CODEC: Codec<GDMode> = StringRepresentable.fromEnum(::values)
    }

    init {
        GDRegistries.GD_MODE_DATA.register(this.serializedName, this.codec)
    }

    @Contract(pure = true)
    override fun getSerializedName(): String = this.name.lowercase()
}
