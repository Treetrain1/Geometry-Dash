package me.treetrain1.geometrydash.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Fallable
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DripstoneThickness

class SpikeBlock(props: Properties) : Block(props), Fallable, SimpleWaterloggedBlock {

    companion object {
        @JvmField
        internal val TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION

        @JvmField
        internal val THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS

        @JvmField
        internal val WATERLOGGED = BlockStateProperties.WATERLOGGED
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(TIP_DIRECTION, Direction.UP)
                .setValue(THICKNESS, DripstoneThickness.TIP)
                .setValue(WATERLOGGED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED)
    }

    override fun stepOn(level: Level, pos: BlockPos, state: BlockState, entity: Entity) {
        super.stepOn(level, pos, state, entity)
        entity.kill()
    }
}
