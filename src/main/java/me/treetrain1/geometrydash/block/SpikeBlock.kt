package me.treetrain1.geometrydash.block

import me.treetrain1.geometrydash.util.isCollidingWithBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DripstoneThickness
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
        internal val THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS

        @JvmField
        internal val WATERLOGGED = BlockStateProperties.WATERLOGGED

        private fun calculateDripstoneThickness(
            level: LevelReader,
            pos: BlockPos,
            dir: Direction,
            isTipMerge: Boolean
        ): DripstoneThickness? {
            val direction = dir.opposite
            val blockState = level.getBlockState(pos.relative(dir))
            if (isPointedDripstoneWithDirection(blockState, direction)) {
                return if (!isTipMerge && blockState.getValue(PointedDripstoneBlock.THICKNESS) != DripstoneThickness.TIP_MERGE) DripstoneThickness.TIP else DripstoneThickness.TIP_MERGE
            } else if (!isPointedDripstoneWithDirection(blockState, dir)) {
                return DripstoneThickness.TIP
            } else {
                val dripstoneThickness = blockState.getValue(PointedDripstoneBlock.THICKNESS)
                if (dripstoneThickness != DripstoneThickness.TIP && dripstoneThickness != DripstoneThickness.TIP_MERGE) {
                    val blockState2 = level.getBlockState(pos.relative(direction))
                    return if (!isPointedDripstoneWithDirection(
                            blockState2,
                            dir
                        )
                    ) DripstoneThickness.BASE else DripstoneThickness.MIDDLE
                } else {
                    return DripstoneThickness.FRUSTUM
                }
            }
        }

        private fun isPointedDripstoneWithDirection(state: BlockState, dir: Direction): Boolean {
            return state.`is`(Blocks.POINTED_DRIPSTONE) && state.getValue(PointedDripstoneBlock.TIP_DIRECTION) == dir
        }

        private fun calculateTipDirection(level: LevelReader, pos: BlockPos, dir: Direction): Direction? {
            val direction: Direction
            if (isValidPointedDripstonePlacement(level, pos, dir)) {
                direction = dir
            } else {
                if (!isValidPointedDripstonePlacement(level, pos, dir.opposite)) {
                    return null
                }

                direction = dir.opposite
            }

            return direction
        }

        private fun isValidPointedDripstonePlacement(level: LevelReader, pos: BlockPos, dir: Direction): Boolean {
            val blockPos = pos.relative(dir.opposite)
            val blockState = level.getBlockState(blockPos)
            return blockState.isFaceSturdy(
                level,
                blockPos,
                dir
            ) || isPointedDripstoneWithDirection(blockState, dir)
        }
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

        val dir = state.getValue(TIP_DIRECTION)
        if (dir == Direction.DOWN && level.blockTicks.hasScheduledTick(pos, this))
            return state;
        else if (direction == dir.opposite && !this.canSurvive(state, level, pos)) {
            if (dir == Direction.DOWN)
                level.scheduleTick(pos, this, 2)
            else
                level.scheduleTick(pos, this, 1)

            return state
        }
        val isTipMerge = state.getValue(THICKNESS) === DripstoneThickness.TIP_MERGE
        val thickness = calculateDripstoneThickness(level, pos, dir, isTipMerge)
        return state.setValue(THICKNESS, thickness)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val pos = context.clickedPos
        val dir = context.nearestLookingVerticalDirection.opposite
        val dir2 = calculateTipDirection(level, pos, dir)
        if (dir2 === null) {
            return null
        }
        val primary = !context.isSecondaryUseActive
        val thickness = calculateDripstoneThickness(level, pos, dir2, primary)
        return if (thickness === null) null
        else defaultBlockState()
            .setValue(PointedDripstoneBlock.TIP_DIRECTION, dir2)
            .setValue(PointedDripstoneBlock.THICKNESS, thickness)
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
    ): VoxelShape? {
        val dripstoneThickness = state.getValue(PointedDripstoneBlock.THICKNESS)
        val voxelShape = if (dripstoneThickness == DripstoneThickness.TIP_MERGE) {
            PointedDripstoneBlock.TIP_MERGE_SHAPE
        } else if (dripstoneThickness == DripstoneThickness.TIP) {
            if (state.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
                PointedDripstoneBlock.TIP_SHAPE_DOWN
            } else {
                PointedDripstoneBlock.TIP_SHAPE_UP
            }
        } else if (dripstoneThickness == DripstoneThickness.FRUSTUM) {
            PointedDripstoneBlock.FRUSTUM_SHAPE
        } else if (dripstoneThickness == DripstoneThickness.MIDDLE) {
            PointedDripstoneBlock.MIDDLE_SHAPE
        } else {
            PointedDripstoneBlock.BASE_SHAPE
        }

        val vec3 = state.getOffset(level, pos)
        return voxelShape.move(vec3.x, 0.0, vec3.z)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        super.entityInside(state, level, pos, entity)
        if (entity !is LivingEntity || !entity.isCollidingWithBlock(level, pos)) return

        entity.kill()
    }
}
