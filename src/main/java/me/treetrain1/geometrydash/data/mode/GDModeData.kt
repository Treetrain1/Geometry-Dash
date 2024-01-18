package me.treetrain1.geometrydash.data.mode

import me.treetrain1.geometrydash.data.GDData
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

abstract class GDModeData {
    @JvmField
    var gdData: GDData? = null

    abstract fun tick()

    @Environment(EnvType.CLIENT)
    abstract fun tickInput(input: Input)

    abstract fun getPose(): Pose

    abstract fun getEntityDimensions(): EntityDimensions

    abstract fun getEyeHeight(): Float

    abstract fun getModelPitch(tickDelta: Float): Float

    abstract fun save(compound: CompoundTag)

    abstract fun load(compound: CompoundTag)
}
