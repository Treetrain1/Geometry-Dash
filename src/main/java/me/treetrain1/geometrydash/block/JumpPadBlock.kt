@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package me.treetrain1.geometrydash.block
import com.mojang.serialization.MapCodec
import gravity_changer.command.LocalDirection
import me.treetrain1.geometrydash.block.entity.JumpPadBlockEntity
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.util.isCollidingWithPad
import me.treetrain1.geometrydash.util.setRelative
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

@Suppress("MemberVisibilityCanBePrivate")
open class JumpPadBlock(val type: JumpPadType, props: Properties) : MultifaceBlock(props), SimpleWaterloggedBlock, EntityBlock {
    companion object {

        @JvmField
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED

        @JvmField
        protected val DOWN: VoxelShape = Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0)
        @JvmField
        protected val UP: VoxelShape = Block.box(4.0, 14.0, 4.0, 12.0, 16.0, 12.0)
        @JvmField
        protected val NORTH: VoxelShape = Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 2.0)
        @JvmField
        protected val SOUTH: VoxelShape = Block.box(4.0, 4.0, 14.0, 12.0, 12.0, 16.0)
        @JvmField
        protected val EAST: VoxelShape = Block.box(14.0, 4.0, 4.0, 16.0, 12.0, 12.0)
        @JvmField
        protected val WEST: VoxelShape = Block.box(0.0, 4.0, 4.0, 2.0, 12.0, 12.0)

        @JvmField
        protected val SHAPE_BY_DIRECTION: Map<Direction, VoxelShape> = mapOf(
            Direction.UP to UP,
            Direction.DOWN to DOWN,
            Direction.NORTH to NORTH,
            Direction.SOUTH to SOUTH,
            Direction.EAST to EAST,
            Direction.WEST to WEST
        )

        private fun calcShape(state: BlockState): VoxelShape {
            var voxelShape = Shapes.empty()

            for (direction in DIRECTIONS) {
                if (hasFace(state, direction)) {
                    voxelShape = Shapes.or(
                        voxelShape,
                        SHAPE_BY_DIRECTION[direction] ?: continue
                    )
                }
            }

            return if (voxelShape.isEmpty) Shapes.block() else voxelShape
        }

        private fun Entity.applyDelta(type: JumpPadType) {
            val delta = this.deltaMovement
            this.setDeltaMovement(delta.x, type.jumpPower, delta.z)
        }

        private fun Entity.vertTeleport() {
            /*
                TODO: make a raycast thing going up relative to the entity gravity
                teleport when it hits
                if it doesn't hit, tp to y 1000 & kill the entity
            */
        }
    }

    private val grower = MultifaceSpreader(this)
    private val shapesCache: Map<BlockState, VoxelShape>

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false))
        this.shapesCache = this.getShapeForEachState(::calcShape)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = this.shapesCache[state] ?: Shapes.block()

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED)
    }

    override fun getSpreader(): MultifaceSpreader = this.grower

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        super.entityInside(state, level, pos, entity)
        if (entity !is LivingEntity || !entity.isCollidingWithPad(level, pos)) return

        val blockEntity = this.blockEntity(level, pos) ?: return
        if (blockEntity.colliding.contains(entity.id)) return

        // TODO: Add a better way to set GD Mode
        if (entity is PlayerDuck) entity.`geometryDash$setGDMode`(true)

        if (type.shouldFlipGravity) {
            entity.setRelative(LocalDirection.UP)
        }
        if (type.shouldJump) {
            entity.setJumping(true)
            entity.applyDelta(type)
        }
        if (type.shouldTeleport) {
            entity.vertTeleport()
        }

        blockEntity.colliding.add(entity.id)
    }

    override fun codec(): MapCodec<out MultifaceBlock>? = null

    override fun isValidStateForPlacement(
        level: BlockGetter,
        state: BlockState,
        pos: BlockPos,
        direction: Direction
    ): Boolean = this.isFaceSupported(direction) && (!state.`is`(this) || !hasFace(state, direction))

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }

        return if (!hasAnyFace(state)) {
            Blocks.AIR.defaultBlockState()
        } else state
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        for (direction in DIRECTIONS) {
            if (hasFace(state, direction)) {
                return true
            }
        }

        return false
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED))
            Fluids.WATER.getSource(false)
        else
            super.getFluidState(state)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = JumpPadBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = BlockEntityTicker { level1, pos, state1, entity ->
        (entity as? JumpPadBlockEntity)?.tick(level1, pos, state1)
    }

    private fun blockEntity(level: Level, pos: BlockPos): JumpPadBlockEntity? = level.getBlockEntity(pos) as? JumpPadBlockEntity

    enum class JumpPadType(
        val shouldJump: Boolean = true,
        val jumpPower: Double = 1.0,
        val shouldFlipGravity: Boolean = false,
        val shouldTeleport: Boolean = false,
    ) {
        LOW(jumpPower = 0.2),
        NORMAL,
        HIGH(jumpPower = 2.0),
        REVERSE_GRAVITY(shouldFlipGravity = true),
        TELEPORT(shouldJump = false, shouldFlipGravity = true, shouldTeleport = true); // Spider vertical teleporting
    }
}
