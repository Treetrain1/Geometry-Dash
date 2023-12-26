@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package me.treetrain1.geometrydash.block
import gravity_changer.command.LocalDirection
import me.treetrain1.geometrydash.block.entity.JumpPadBlockEntity
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.util.setRelative
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HalfTransparentBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
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
open class JumpPadBlock(val type: JumpPadType, props: Properties) : HalfTransparentBlock(props), SimpleWaterloggedBlock, EntityBlock {
    companion object {
        private const val COOLDOWN: Int = 3
        private const val JUMP_POWER: Double = 1.0

        @JvmField
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED

        @JvmField
        protected val SHAPE: VoxelShape = Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0)

        private fun Entity.applyDelta() {
            val delta = this.deltaMovement
            this.setDeltaMovement(delta.x, JUMP_POWER, delta.z)
        }
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        super.entityInside(state, level, pos, entity)
        if (entity !is LivingEntity) return

        val blockEntity = this.blockEntity(level, pos) ?: return
        if (blockEntity.cooldowns.containsKey(entity.uuid.toString())) return

        // TODO: Add a better way to set GD Mode
        if (entity is Player) (entity as PlayerDuck).`geometryDash$setGDMode`(true)

        if (type.shouldJump) {
            entity.setJumping(true)
            entity.applyDelta()
        }
        if (type.shouldFlipGravity) {
            entity.setRelative(LocalDirection.UP)
        }

        blockEntity.cooldowns[entity.uuid.toString()] = COOLDOWN
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED))
            Fluids.WATER.getSource(true)
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

    enum class JumpPadType {
        LOW,
        NORMAL,
        HIGH,
        REVERSE_GRAVITY,
        TELEPORT; // Spider vertical teleporting

        val shouldJump: Boolean
            get() = this == LOW || this == NORMAL || this == HIGH || this == REVERSE_GRAVITY

        val jumpPower: Double
            get() =
                when (this) {
                    LOW -> 0.2
                    NORMAL, REVERSE_GRAVITY -> 1.0
                    HIGH -> 2.0
                    else -> 0.0
                }

        val shouldFlipGravity: Boolean
            get() = this == REVERSE_GRAVITY || this == TELEPORT
    }
}
