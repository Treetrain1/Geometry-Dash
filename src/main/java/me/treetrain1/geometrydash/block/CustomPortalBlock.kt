package me.treetrain1.geometrydash.block

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.state.BlockState

@Suppress("OVERRIDE_DEPRECATION")
open class CustomPortalBlock(
    @JvmField val fromDimension: ResourceKey<Level>,
    @JvmField val toDimension: ResourceKey<Level>,
    properties: Properties
) : NetherPortalBlock(properties) {

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
    }
}
