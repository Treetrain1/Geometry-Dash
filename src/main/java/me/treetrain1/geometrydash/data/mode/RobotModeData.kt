package me.treetrain1.geometrydash.data.mode

import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.launch
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

open class RobotModeData : GDModeData() {
    private var targetCubeRot: Float = 0F
    private var cubeRot: Float = 0F
    private var prevCubeRot: Float = 0F
    private var thrustAmount = 1.0
    private var inProgress: Boolean = false

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@RobotModeData.targetCubeRot = Math.round(this@RobotModeData.targetCubeRot / 90F) * 90F
                } else {
                    val gravity = this.player.gravity
                    this@RobotModeData.targetCubeRot += if (gravity != null && gravity < 0) -20 else 20
                }
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(input: Input): Boolean {
        val data = this.gdData!!
        val player = data.player
        if (data.canProcessInput) {
            if (this.inProgress) {
                this.thrustAmount *= 0.9
                if (thrustAmount < 0.05) {
                    this.inProgress = false
                    this.thrustAmount = 1.0
                    return true
                }
                player.launch(this.thrustAmount)
                return false
            } else if (player.onGround()) {
                this.thrustAmount = 1.0
                this.inProgress = true
                player.launch(this.thrustAmount)
                return false
            }
        } else {
            this.inProgress = false
            this.thrustAmount = 1.0
            return true
        }
        return false
    }

    override fun onRingUnlock() {
        this.inProgress = false
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
