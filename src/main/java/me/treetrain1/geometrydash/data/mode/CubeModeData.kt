package me.treetrain1.geometrydash.data.mode

import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth

class CubeModeData : GDModeData() {
    private var targetCubeRot: Float = 0F
    private var cubeRot: Float = 0F
    private var prevCubeRot: Float = 0F

    override fun tick() {
        this.prevCubeRot = this.cubeRot
        this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.25F
    }

    override fun tickInput(input: Input) {
        this.gdData?.run {
            if (this.player.onGround()) {
                this@CubeModeData.targetCubeRot = Math.round(this@CubeModeData.targetCubeRot / 90F) * 90F
            } else {
                this@CubeModeData.targetCubeRot += 20F
            }
        }
    }

    override fun getModelPitch(tickDelta: Float): Float {
        return Mth.lerp(tickDelta, this.prevCubeRot, this.cubeRot)
    }

    override fun save(compound: CompoundTag) {
        compound.putFloat("target_rotation", this.targetCubeRot)
    }

    override fun load(compound: CompoundTag) {
        this.targetCubeRot = compound.getFloat("target_rotation")
        this.prevCubeRot = this.targetCubeRot
        this.cubeRot = this.targetCubeRot
    }
}
