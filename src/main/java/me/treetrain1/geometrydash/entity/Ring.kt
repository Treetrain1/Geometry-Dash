package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.util.gdData
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

// Usually called Orbs but in the files I believe it's called Rings
open class Ring(
    type: EntityType<out Ring>,
    level: Level
) : StaticEntity(type, level) {

    companion object {
        @PublishedApi
        @JvmField
        internal val TYPE: EntityDataAccessor<RingType> = SynchedEntityData.defineId(Ring::class.java, RingType.SERIALIZER)

        protected fun Entity.flipGravity() {
            // TODO: implement or just use the thing from jump pads
        }

        protected fun Entity.bounce(strength: Double) {
            // TODO: implement
        }

        protected fun Entity.teleport() {
            // TODO: implement
        }
    }

    inline var type: RingType
        get() = this.`access$entityData`[TYPE]
        set(value) { this.`access$entityData`[TYPE] = value }

    override fun defineSynchedData() {
        this.entityData.define(TYPE, RingType.BOUNCE)
    }

    open fun onApply(player: Player, dat: GDData) {
        dat.ringLocked = true

        val type = this.type
        if (type.shouldBounce) {
            player.bounce(type.bounceStrength)
        }
        if (type.shouldFlipGravity) {
            player.flipGravity()
        }
        if (type.shouldTeleport) {
            player.teleport()
        }
    }

    override fun tick() {
        if (this.level().isClientSide)
            this.applyToPlayers()
    }

    protected open fun applyToPlayers() {
        val list = this.level().getEntitiesOfClass(Player::class.java, this.boundingBox)
        for (player in list) {
            val gdData = player.gdData
            if (!gdData.playingGD) continue

            val gdModeData = gdData.gdModeData ?: continue
            if (!gdData.ignoreInput && !gdData.ringLocked && gdData.inputBuffer)
                this.onApply(player, gdData)
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("Type", this.type.name)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        try {
            this.type = RingType.valueOf(compound.getString("Type"))
        } catch (_: IllegalArgumentException) {
            this.type = RingType.BOUNCE
        }
    }

    // TODO: double check the names
    enum class RingType(
        val shouldBounce: Boolean = true,
        val bounceStrength: Double = 1.0,
        val shouldFlipGravity: Boolean = false,
        val shouldTeleport: Boolean = false,
    ) {
        SMALL_BOUNCE(bounceStrength = 0.5), // purple/pink
        BOUNCE, // yellow
        BIG_BOUNCE(bounceStrength = 1.5), // red
        SWING(shouldBounce = false, shouldFlipGravity = true), // green
        REVERSE_GRAVITY(shouldFlipGravity = true), // blue
        TELEPORT(shouldBounce = false, shouldFlipGravity = true, shouldTeleport = true); // pink with arrows

        companion object {
            @JvmField
            val SERIALIZER: EntityDataSerializer<RingType> = EntityDataSerializer.simpleEnum(RingType::class.java)
        }
    }

    @PublishedApi
    internal val `access$entityData`: SynchedEntityData
        get() = entityData
}
