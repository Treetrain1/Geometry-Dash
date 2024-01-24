package me.treetrain1.geometrydash.block

import me.treetrain1.geometrydash.util.isCollidingWithBlockShape
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Fallable
import net.minecraft.world.level.block.PointedDripstoneBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class SpikeBlock(props: Properties) : Block(props), Fallable, SimpleWaterloggedBlock {

    companion object {
        @JvmField
        internal val TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION

        @JvmField
        internal val SIZE = IntegerProperty.create("size", 1, 3)

        @JvmField
        internal val WATERLOGGED = BlockStateProperties.WATERLOGGED

        @JvmField
        val SMALL_SHAPE = box(7.0, 10.0, 7.0, 9.0, 14.5, 9.0);

        @JvmField
        val MIDDLE_SHAPE = box(5.5, 10.0, 5.5, 10.5, 14.5, 10.5);

        @JvmField
        val LARGE_SHAPE = box(4.0, 10.0, 4.0, 12.0, 14.5, 12.0);
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(TIP_DIRECTION, Direction.UP)
                .setValue(SIZE, 3)
                .setValue(WATERLOGGED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TIP_DIRECTION, SIZE, WATERLOGGED)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))

        return state
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val pos = context.clickedPos
        val dir = context.nearestLookingVerticalDirection.opposite
        val size = 3
        return defaultBlockState()
            .setValue(PointedDripstoneBlock.TIP_DIRECTION, dir)
            .setValue(SIZE, size)
            .setValue(PointedDripstoneBlock.WATERLOGGED, level.getFluidState(pos).type === Fluids.WATER)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return Shapes.empty()
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val size = state.getValue(SIZE)
        val shape = when (size) {
            1 -> SMALL_SHAPE
            2 -> MIDDLE_SHAPE
            3 -> LARGE_SHAPE
            else -> LARGE_SHAPE
        }

        val vec3 = state.getOffset(level, pos)
        return shape.move(vec3.x, 0.0, vec3.z)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        super.entityInside(state, level, pos, entity)
        if (entity !is LivingEntity || entity.isDeadOrDying || !entity.isCollidingWithBlockShape(level, pos)) return

        entity.kill()
    }
}
