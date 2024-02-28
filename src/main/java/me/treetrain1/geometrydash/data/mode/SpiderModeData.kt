package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.setRelativeGravity
import me.treetrain1.geometrydash.util.vertTeleport
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

open class SpiderModeData : GDModeData() {

    override val mode: GDMode = GDMode.SPIDER

    private var targetCubeRot: Float = 0F
    private var cubeRot: Float = 0F
    private var prevCubeRot: Float = 0F

    companion object {
        @JvmField
        val CODEC: Codec<SpiderModeData> = Codec.unit(::SpiderModeData)
    }

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@SpiderModeData.targetCubeRot = Math.round(this@SpiderModeData.targetCubeRot / 90F) * 90F
                } else {
                    val gravity = this.player.gravity
                    this@SpiderModeData.targetCubeRot += if (gravity.y < 0) -20 else 20
                }
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(input: Input): Boolean {
        val data = this.gdData!!
        val player = data.player
        if (data.canProcessInput && player.onGround()) {
            player.setRelativeGravity(true)
            player.vertTeleport(player.level())
            return true
        }
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
