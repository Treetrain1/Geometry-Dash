package me.treetrain1.geometrydash.data.mode

import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag

class CubeModeData : GDModeData() {
    private var cubeRot: Float = 0f

    override fun tick() {}

    override fun tickInput(input: Input) {
        this.gdData?.run {
            if (this.player.onGround()) {
                this@CubeModeData.cubeRot = Math.round(this@CubeModeData.cubeRot / 90f) * 90f
            } else {
                this@CubeModeData.cubeRot += 20
            }
        }
    }

    override fun getModelPitch(tickDelta: Float): Float {
        return this.cubeRot
    }

    override fun save(compound: CompoundTag) {
        compound.putFloat("rotation", this.cubeRot)
    }

    override fun load(compound: CompoundTag) {
        this.cubeRot = compound.getFloat("rotation")
    }
}
