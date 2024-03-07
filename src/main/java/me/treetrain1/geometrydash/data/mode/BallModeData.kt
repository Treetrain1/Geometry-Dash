package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.gravityDirection
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose

open class BallModeData(
    private var targetCubeRot: Float = 0F,
    private var cubeRot: Float = 0F,
    private var prevCubeRot: Float = 0F
) : GDModeData() {

    override val mode: GDMode = GDMode.BALL

    companion object {
        @JvmField
        val CODEC: Codec<BallModeData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("target_rot").forGetter(BallModeData::targetCubeRot),
                Codec.FLOAT.fieldOf("rot").forGetter(BallModeData::cubeRot),
                Codec.FLOAT.fieldOf("prev_rot").forGetter(BallModeData::prevCubeRot)
            ).apply(instance, ::BallModeData)
        }
    }

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                val gravity = this.player.gravity
                this@BallModeData.targetCubeRot += if (gravity.y < 0) -10 else 10
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(): Boolean {
        val data = this.gdData!!
        val player = data.player
        if (data.canProcessInput && player.onGround()) {
            player.gravityDirection = player.gravityDirection.opposite
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
