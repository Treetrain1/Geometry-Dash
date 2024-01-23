package me.treetrain1.geometrydash.data.mode

import me.treetrain1.geometrydash.entity.pose.GDPoses
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

open class CubeModeData : GDModeData() {
    private var targetCubeRot: Float = 0F
    private var cubeRot: Float = 0F
    private var prevCubeRot: Float = 0F

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@CubeModeData.targetCubeRot = Math.round(this@CubeModeData.targetCubeRot / 90F) * 90F
                } else {
                    this@CubeModeData.targetCubeRot += 20F
                }
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(input: Input): Boolean {
        // TODO: Move vanilla jump behavior here
    }

    override fun getPose(): Pose {
        return GDPoses.CUBE!!
    }

    override fun getEntityDimensions(): EntityDimensions {
        return EntityDimensions.scalable(0.85F, 0.85F)
    }

    override fun getEyeHeight(): Float {
        return 0.425F
    }

    override fun getCameraYOffset(): Float {
        return 0F
    }

    override fun getModelPitch(tickDelta: Float): Float {
        return Mth.lerp(tickDelta, this.prevCubeRot, this.cubeRot)
    }

    override fun save(compound: CompoundTag): CompoundTag {
        return compound
    }

    override fun load(compound: CompoundTag): CompoundTag {
        return compound
    }
}
