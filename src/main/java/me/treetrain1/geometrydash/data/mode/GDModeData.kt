package me.treetrain1.geometrydash.data.mode

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.registry.GDRegistries
import me.treetrain1.geometrydash.registry.register
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.player.Input
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

fun CompoundTag.putGDModeData(key: String, value: GDModeData?) {
    val tag = value?.toTag() ?: return
    this.put(key, tag)
}

fun CompoundTag.getGDModeData(key: String): GDModeData? {
    val compound = this.getCompound(key)
    return compound.toGDModeData()
}

fun GDModeData.toTag(): Tag? {
    val dataResult: DataResult<Tag> = GDModeData.CODEC.encodeStart(NbtOps.INSTANCE, this)
    return dataResult.result().getOrNull()
}

fun CompoundTag.toGDModeData(): GDModeData? {
    val modeDataResult: DataResult<Pair<GDModeData, Tag>> = GDModeData.CODEC.decode(NbtOps.INSTANCE, this)
    return modeDataResult.result().getOrNull()?.first
}

abstract class GDModeData {

    companion object {
        @JvmField
        val CODEC: Codec<GDModeData> = GDRegistries.GD_MODE_DATA
            .byNameCodec()
            .dispatch(GDModeData::codec, Function.identity())

        fun bootstrap(registry: Registry<Codec<out GDModeData>>) {
            registry.register("cube", CubeModeData.CODEC)
            registry.register("ship", ShipModeData.CODEC)
            registry.register("ball", BallModeData.CODEC)
            registry.register("ufo", UFOModeData.CODEC)
            registry.register("wave", WaveModeData.CODEC)
            registry.register("robot", RobotModeData.CODEC)
            registry.register("spider", SpiderModeData.CODEC)
            registry.register("swing", SwingModeData.CODEC)
            registry.register("cube_3d", Cube3DModeData.CODEC)
        }
    }

    @JvmField
    var gdData: GDData? = null

    abstract fun tick()

    open fun flying(): Boolean {
        return false
    }

    /**
     * Whether or not the mode locks the buffer after a successful input
     */
    @Environment(EnvType.CLIENT)
    open fun lockOnSuccess(): Boolean {
        return true
    }

    /**
     * Whether or not the buffer lock should be cleared after an input release
     * <p>
     * Useful for modes like UFO
     */
    @Environment(EnvType.CLIENT)
    open fun unlockOnRelease(): Boolean {
        return true
    }

    open fun onRingUnlock() {}

    /**
     * @return if the input was successful
     */
    @Environment(EnvType.CLIENT)
    abstract fun tickInput(input: Input): Boolean

    abstract fun getPose(): Pose

    abstract fun getEntityDimensions(): EntityDimensions

    abstract fun getEyeHeight(): Float

    abstract fun getCameraYOffset(): Float

    open fun preventDrowning(): Boolean {
        return true
    }

    open fun destroyWhenTouchingWater(): Boolean {
        return false
    }

    open fun destroyWhenTouchingLava(): Boolean {
        return true
    }

    open fun useGDCamera(): Boolean {
        return true
    }

    open fun lockCamera(): Boolean {
        return true
    }

    open fun lockForwardsMovement(): Boolean {
        return true
    }

    open fun allowSidewaysMovement(): Boolean {
        return false
    }

    abstract fun getModelPitch(tickDelta: Float): Float

    abstract val codec: Codec<out GDModeData>

    abstract fun save(compound: CompoundTag): CompoundTag

    abstract fun load(compound: CompoundTag): CompoundTag
}
