package me.treetrain1.geometrydash.data.mode

import me.treetrain1.geometrydash.data.GDData
import net.minecraft.nbt.CompoundTag

abstract class AbstractGDModeData {
    @JvmField
    protected var gdData: GDData? = null

    abstract fun tick()

    abstract fun onJump()

    abstract fun onFall()

    abstract fun onLand()

    abstract fun getModelPitch(tickDelta: Float): Float

    abstract fun save(compound: CompoundTag)

    abstract fun load(compound: CompoundTag)

    fun setGdData(gdData: GDData?) {
        this.gdData = gdData
    }
}
