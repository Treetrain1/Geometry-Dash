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

open class Robot3DModeData : RobotModeData() {

    override val mode: GDMode = GDMode.ROBOT_3D

    companion object {
        @JvmField
        val CODEC: Codec<Robot3DModeData> = Codec.unit(::Robot3DModeData)
    }

    override val withstandsCollisions: Boolean = true

    override val codec: Codec<out GDModeData> = CODEC
}
