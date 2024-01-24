package me.treetrain1.geometrydash.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

open class StaticEntity(
    type: EntityType<out StaticEntity>,
    level: Level,
) : Entity(type, level) {

    init {
        this.noPhysics = true
    }

    override fun defineSynchedData() {}

    override fun tick() {}

    override fun baseTick() {}

    override fun readAdditionalSaveData(compound: CompoundTag) {}

    override fun addAdditionalSaveData(compound: CompoundTag) {}

    override fun waterSwimSound() {}

    override fun playStepSound(pos: BlockPos, state: BlockState) {}

    override fun playMuffledStepSound(state: BlockState) {}

    override fun playEntityOnFireExtinguishedSound() {}

    override fun checkInsideBlocks() {}

    override fun nextStep(): Float = 0F

    override fun canCollideWith(entity: Entity): Boolean = false

    override fun isColliding(pos: BlockPos, state: BlockState): Boolean = false

    override fun isPushedByFluid(): Boolean = false

    override fun isPushable(): Boolean = false

    override fun isPickable(): Boolean = true

    override fun processFlappingMovement() {}

    override fun tryCheckInsideBlocks() {}

    override fun isNoGravity(): Boolean = true

    override fun gameEvent(event: GameEvent, entity: Entity?) {}

    override fun gameEvent(event: GameEvent) {}
}
