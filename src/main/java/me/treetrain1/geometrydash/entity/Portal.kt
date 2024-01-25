package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.util.gdData
import me.treetrain1.geometrydash.util.launch
import me.treetrain1.geometrydash.util.setRelative
import me.treetrain1.geometrydash.util.vertTeleport
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

open class Portal(
    type: EntityType<out Portal>,
    level: Level
) : StaticEntity(type, level) {

    companion object {
        @PublishedApi
        @JvmField
        internal val TYPE: EntityDataAccessor<PortalType> = SynchedEntityData.defineId(Portal::class.java, PortalType.SERIALIZER)
    }

    inline var type: PortalType
        get() = this.`access$entityData`[TYPE]
        set(value) { this.`access$entityData`[TYPE] = value }

    override fun defineSynchedData() {
        this.entityData.define(TYPE, PortalType.CUBE)
    }

    open fun onApply(player: Player, dat: GDData) {
        dat.ringLocked = true

        val type = this.type
        val mode = type.modeSwitch
        val scale = type.scale

        if (mode != null)
            dat.mode = mode

        if (type.shouldFlipGravity)
            player.setRelative(true)

        if (scale != null)
            dat.scale = scale
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

            this.onApply(player, gdData)
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("Type", this.type.name)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        try {
            this.type = PortalType.valueOf(compound.getString("Type"))
        } catch (_: IllegalArgumentException) {
            this.type = PortalType.CUBE
        }
    }

    enum class PortalType(
        val modeSwitch: GDMode? = null,
        val shouldFlipGravity: Boolean = false,
        val scale: Double? = null,
    ) {
        CUBE(modeSwitch = GDMode.CUBE),
        SHIP(modeSwitch = GDMode.SHIP),
        BALL(modeSwitch = GDMode.BALL),
        UFO(modeSwitch = GDMode.UFO),
        WAVE(modeSwitch = GDMode.WAVE),
        ROBOT(modeSwitch = GDMode.ROBOT),
        SPIDER(modeSwitch = GDMode.SPIDER),
        SWING(modeSwitch = GDMode.SWING),
        CUBE_3D(modeSwitch = GDMode.CUBE_3D),

        GRAVITY_FLIP(shouldFlipGravity = true),

        SCALE_NORMAL(scale = 1.0),
        SCALE_SMALL(scale = 0.5),
        SCALE_LARGE(scale = 2.0);

        companion object {
            @JvmField
            val SERIALIZER: EntityDataSerializer<PortalType> = EntityDataSerializer.simpleEnum(PortalType::class.java)
        }
    }

    @PublishedApi
    internal val `access$entityData`: SynchedEntityData
        get() = entityData
}
