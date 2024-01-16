package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.AbstractGDModeData
import me.treetrain1.geometrydash.data.mode.CubeModeData
import net.minecraft.util.StringRepresentable
import org.jetbrains.annotations.Contract
import java.util.function.Supplier

enum class GDMode(val modeDataSupplier: () -> AbstractGDModeData) : StringRepresentable {
    CUBE({ CubeModeData() }),
    SHIP({ CubeModeData() }),
    BALL({ CubeModeData() }),
    UFO({ CubeModeData() }),
    WAVE({ CubeModeData() }),
    ROBOT({ CubeModeData() }),
    SPIDER({ CubeModeData() }),
    SWING({ CubeModeData() });

    @Contract(pure = true)
    override fun toString(): String {
        return this.name
    }

    @Contract(pure = true)
    override fun getSerializedName(): String {
        return this.name
    }
}
