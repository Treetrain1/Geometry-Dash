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

    open fun flying(): Boolean {
        return false
    }

    /**
     * Whether or not the mode locks the buffer after a successful input
     */
    @Environment(EnvType.CLIENT)
    open fun lockOnSuccess(): Boolean {
        return true
    }

    /**
     * Whether or not the buffer lock should be cleared after an input release
     * <p>
     * Useful for modes like UFO
     */
    @Environment(EnvType.CLIENT)
    open fun unlockOnRelease(): Boolean {
        return true
    }

    open fun onRingUnlock() {}

    /**
     * @return if the input was successful
     */
    @Environment(EnvType.CLIENT)
    abstract fun tickInput(input: Input): Boolean

    abstract fun getPose(): Pose

    abstract fun getEntityDimensions(): EntityDimensions

    abstract fun getEyeHeight(): Float

    abstract fun getCameraYOffset(): Float

    open fun preventDrowning(): Boolean {
        return true
    }

    open fun destroyWhenTouchingWater(): Boolean {
        return false
    }

    open fun destroyWhenTouchingLava(): Boolean {
        return true
    }

    open fun useGDCamera(): Boolean {
        return true
    }

    open fun lockCamera(): Boolean {
        return true
    }

    open fun lockForwardsMovement(): Boolean {
        return true
    }

    open fun allowSidewaysMovement(): Boolean {
        return false
    }

    abstract fun getModelPitch(tickDelta: Float): Float

    abstract fun save(compound: CompoundTag): CompoundTag

    abstract fun load(compound: CompoundTag): CompoundTag
}
