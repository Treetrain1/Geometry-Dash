package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.launch
import me.treetrain1.geometrydash.util.setRelativeDelta
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.phys.Vec3

open class SwingModeData : GDModeData() {

    override val mode: GDMode = GDMode.SWING

    private var targetCubeRot: Float = 0F
    private var cubeRot: Float = 0F
    private var prevCubeRot: Float = 0F

    companion object {
        @JvmField
        val CODEC: Codec<SwingModeData> = Codec.unit(::SwingModeData)
    }

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@SwingModeData.targetCubeRot = Math.round(this@SwingModeData.targetCubeRot / 90F) * 90F
                } else {
                    val gravity = this.player.gravity
                    this@SwingModeData.targetCubeRot += if (gravity.y < 0) -24 else 24
                }
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(): Boolean {
        val data = this.gdData!!
        val player = data.player
        val delta = player.deltaMovement
        if (data.canProcessInput) {
            player.addDeltaMovement(Vec3(0.0, 0.15, 0.0))
            player.hasImpulse = true
            return true
        }
        return false
    }

    override fun lockOnSuccess(): Boolean {
        return false
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

    override val codec: Codec<out GDModeData> = CODEC

    override fun save(compound: CompoundTag): CompoundTag {
        return compound
    }

    override fun load(compound: CompoundTag): CompoundTag {
        return compound
    }
}
