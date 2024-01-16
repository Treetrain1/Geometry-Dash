package me.treetrain1.geometrydash.data.mode

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth

class CubeModeData : AbstractGDModeData() {
    private var targetCubeRotation = 0f
    private var cubeRotation = 0f
    private var prevCubeRotation = 0f

    override fun tick() {
        this.prevCubeRotation = this.cubeRotation
        this.cubeRotation += (this.targetCubeRotation - this.cubeRotation) * 0.25f
        this.targetCubeRotation %= 360
        this.cubeRotation %= 360
        this.prevCubeRotation %= 360
    }

    override fun onJump() {
        this.targetCubeRotation += 180f
    }

    override fun onFall() {
        if (!gdData!!.isInJump) this.targetCubeRotation += 90f
    }

    override fun onLand() {
    }

    override fun getModelPitch(tickDelta: Float): Float {
        return Mth.lerp(tickDelta, this.prevCubeRotation, this.cubeRotation)
    }

    override fun save(compound: CompoundTag) {
        compound.putFloat("target_rotation", this.targetCubeRotation)
        compound.putFloat("rotation", this.cubeRotation)
        compound.putFloat("prev_rotation", this.prevCubeRotation)
    }

    override fun load(compound: CompoundTag) {
        this.targetCubeRotation = compound.getFloat("target_rotation")
        this.cubeRotation = compound.getFloat("rotation")
        this.prevCubeRotation = compound.getFloat("prev_rotation")
    }
}
