package me.treetrain1.geometrydash.data.mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.entity.pose.GDPoses
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.launch
import net.minecraft.client.player.Input
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.phys.Vec3

open class CubeModeData(
    private var targetCubeRot: Float = 0F,
    private var cubeRot: Float = 0F,
    private var prevCubeRot: Float = 0F,
) : GDModeData() {

    companion object {
        @JvmField
        val CODEC: Codec<CubeModeData> = Codec.unit<CubeModeData>(::CubeModeData) /*RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("targetRot").forGetter(CubeModeData::targetCubeRot),
                Codec.FLOAT.fieldOf("rot").forGetter(CubeModeData::cubeRot),
                Codec.FLOAT.fieldOf("prevRot").forGetter(CubeModeData::prevCubeRot)
            ).apply(instance, ::CubeModeData)
        }*/
    }

    override fun tick() {
        if (this.gdData?.player?.level()?.isClientSide == true) {
            this.prevCubeRot = this.cubeRot
            this.gdData?.run {
                if (this.player.onGround()) {
                    this@CubeModeData.targetCubeRot = Math.round(this@CubeModeData.targetCubeRot / 90F) * 90F
                } else {
                    val gravity = this.player.gravity
                    this@CubeModeData.targetCubeRot += if (gravity.y < 0) -24 else 24
                }
            }
            this.cubeRot += (this.targetCubeRot - this.cubeRot) * 0.395F // both 0.395F and 0.45F seem alright, up to you tree
        }
    }

    override fun tickInput(input: Input): Boolean {
        val data = this.gdData!!
        val player = data.player
        if (data.canProcessInput && player.onGround()) {
            player.launch(1.0)
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
