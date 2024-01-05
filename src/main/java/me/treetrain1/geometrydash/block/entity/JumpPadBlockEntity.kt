package me.treetrain1.geometrydash.block.entity

import me.treetrain1.geometrydash.registry.RegisterBlockEntities
import me.treetrain1.geometrydash.util.isCollidingWithPad
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

open class JumpPadBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(RegisterBlockEntities.JUMP_PAD, pos, blockState) {

    /**
     * List of currently colliding entities.
     */
    @JvmField
    val colliding: MutableList<Int> = mutableListOf()

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        colliding.removeAll { entityId ->
            val entity = level.getEntity(entityId) ?: return@removeAll true
            return@removeAll !entity.isCollidingWithPad(level, pos)
        }
    }
}
