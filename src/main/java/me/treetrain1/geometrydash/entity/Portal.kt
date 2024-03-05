package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.util.gdData
import me.treetrain1.geometrydash.util.setRelativeGravity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.StringRepresentable
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

    open fun onApply(player: Player, data: GDData) {
        val type = this.type
        val mode = type.modeSwitch
        val scale = type.scale

        if (mode != null)
            data.mode = mode

        if (type.shouldFlipGravity)
            player.setRelativeGravity(true)

        if (type.shouldMirror)
            data.mirrorCamera()

        if (scale != null)
            data.scale = scale
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
        compound.putString("type", this.type.serializedName)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        try {
            this.type = PortalType.valueOf(compound.getString("type").uppercase())
        } catch (_: IllegalArgumentException) {
            this.type = PortalType.CUBE
        }
    }

    enum class PortalType(
        val modeSwitch: GDMode? = null,
        val shouldFlipGravity: Boolean = false,
        val shouldMirror: Boolean = false,
        val scale: Float? = null,
    ) : StringRepresentable {
        CUBE(modeSwitch = GDMode.CUBE),
        SHIP(modeSwitch = GDMode.SHIP),
        BALL(modeSwitch = GDMode.BALL),
        UFO(modeSwitch = GDMode.UFO),
        WAVE(modeSwitch = GDMode.WAVE),
        ROBOT(modeSwitch = GDMode.ROBOT),
        SPIDER(modeSwitch = GDMode.SPIDER),
        SWING(modeSwitch = GDMode.SWING),
        CUBE_3D(modeSwitch = GDMode.CUBE_3D),
        ROBOT_3D(modeSwitch = GDMode.ROBOT_3D),

        GRAVITY_FLIP(shouldFlipGravity = true),

        MIRROR(shouldMirror = true),

        SCALE_NORMAL(scale = 1F),
        SCALE_SMALL(scale = 0.5F),
        SCALE_LARGE(scale = 2F);

        companion object {
            @JvmField
            val SERIALIZER: EntityDataSerializer<PortalType> = EntityDataSerializer.simpleEnum(PortalType::class.java)
        }

        override fun getSerializedName(): String = this.name.lowercase()
    }

    @PublishedApi
    internal val `access$entityData`: SynchedEntityData
        get() = entityData
}
