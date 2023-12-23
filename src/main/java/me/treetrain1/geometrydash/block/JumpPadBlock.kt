@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package me.treetrain1.geometrydash.block
import gravity_changer.api.GravityChangerAPI
import gravity_changer.util.RotationUtil
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HalfTransparentBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3

open class JumpPadBlock(props: Properties) : HalfTransparentBlock(props), SimpleWaterloggedBlock {
    companion object {
        @JvmField
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED)
    }

    override fun stepOn(level: Level, pos: BlockPos, state: BlockState, entity: Entity) {
        super.stepOn(level, pos, state, entity)

        if (entity !is LivingEntity) return
        entity.setJumping(true)

        val vec = RotationUtil.vecPlayerToWorld(0.0, 1.0, 0.0, GravityChangerAPI.getGravityDirection(entity))
        entity.addDeltaMovement(vec)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED))
            Fluids.WATER.getSource(true)
        else
            super.getFluidState(state)
    }
}
