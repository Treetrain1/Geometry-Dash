package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.launch
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

open class RobotModeData : GDModeData() {

    override val mode: GDMode = GDMode.ROBOT

    private var targetRot: Float = 0F
    private var rot: Float = 0F
    private var prevRot: Float = 0F
    private var thrustAmount = 1.0
    private var inProgress: Boolean = false

    companion object {
        @JvmField
        val CODEC: Codec<RobotModeData> = Codec.unit(::RobotModeData)
    }

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevRot = this.rot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@RobotModeData.targetRot = Math.round(this@RobotModeData.targetRot / 90F) * 90F
                } else {
                    val gravity = this.player.gravity
                    this@RobotModeData.targetRot += if (gravity.y < 0) -20 else 20
                }
            }
            this.rot += (this.targetRot - this.rot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(): Boolean {
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

    override fun onOrbUnlock() {
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
        return Mth.lerp(tickDelta, this.prevRot, this.rot)
    }

    override val codec: Codec<out GDModeData> = CODEC

    override fun save(compound: CompoundTag): CompoundTag {
        return compound
    }

    override fun load(compound: CompoundTag): CompoundTag {
        return compound
    }
}
