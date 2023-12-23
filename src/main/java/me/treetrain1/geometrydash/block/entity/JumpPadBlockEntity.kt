package me.treetrain1.geometrydash.block.entity

import me.treetrain1.geometrydash.registry.RegisterBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

open class JumpPadBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(RegisterBlockEntities.JUMP_PAD, pos, blockState) {

    /**
     * Cooldowns for each entity UUID.
     */
    val cooldowns: MutableMap<String, Int> = mutableMapOf()

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        for ((uuid, cooldown) in cooldowns) {
            if (cooldown <= 0) {
                cooldowns.remove(uuid)
                continue
            }
            cooldowns[uuid] = cooldown - 1
        }
    }
}
