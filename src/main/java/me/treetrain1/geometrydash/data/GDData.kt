package me.treetrain1.geometrydash.data

import it.unimi.dsi.fastutil.ints.IntArrayList
import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.gravity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import virtuoel.pehkui.api.ScaleTypes

@Suppress("MemberVisibilityCanBePrivate")
open class GDData @JvmOverloads constructor(
    @JvmField val player: Player,
    @JvmField var gdModeData: GDModeData? = null,
    @JvmField var checkpoints: MutableList<CheckpointSnapshot> = mutableListOf()
) {

    companion object {
        private const val MODE_TAG = "Mode"
        private const val MODE_DATA_TAG = "ModeData"
        private const val SCALE_TAG = "Scale"
        private const val CHECKPOINTS_TAG = "Checkpoints"
        private const val DASH_RING_TAG = "DashRingID"

        @JvmField
        val GD_DATA: EntityDataAccessor<in CompoundTag> = SynchedEntityData.defineId(
            Player::class.java, EntityDataSerializers.COMPOUND_TAG
        )

    }

    val mode: GDMode = GDMode.CUBE_3D

    inline var scale: Float
        get() = ScaleTypes.WIDTH.getScaleData(this.player).scale
        set(value) {
            val width = ScaleTypes.WIDTH.getScaleData(this.player)
            val height = ScaleTypes.HEIGHT.getScaleData(this.player)
            width.targetScale = value
            height.targetScale = value
        }

    @JvmField
    var isVisible: Boolean = true

    @JvmField
    var timeMod: Float = 1F

    @JvmField
    var dirty: Boolean = true

    inline val playingGD: Boolean
        get() = this.mode != null

    /**
     * Whether or not player input is ignored
     * <p>
     * May be useful in Auto levels
     */
    @Environment(EnvType.CLIENT)
    @JvmField
    var ignoreInput: Boolean = false

    /**
     * Whether or not the jump button is being held
     */
    @Environment(EnvType.CLIENT)
    @JvmField
    var inputBuffer: Boolean = false

    /**
     * Whether or not an input release is required for the next input
     */
    @Environment(EnvType.CLIENT)
    @JvmField
    var bufferLocked: Boolean = false

    /**
     * Whether or not an input release is required for Ring interaction
     */
    @Environment(EnvType.CLIENT)
    @JvmField
    var ringLocked: Boolean = false

    @JvmField
    var dashRingID: String = ""

    inline val isDashing: Boolean get() = dashRingID.isNotEmpty()

    @PublishedApi
    internal inline val level: Level get() = this.player.level()

    inline val canProcessInput: Boolean get() {
        if (ignoreInput || bufferLocked) return false
        return inputBuffer
    }

    inline val canBounceFromRing: Boolean get() = !(ignoreInput || ringLocked || isDashing) && inputBuffer

    inline val lastValidCheckpoint: CheckpointSnapshot? get() {
        val level = this.level
        checkpoints.removeAll { checkpoint ->
            val entity: Entity? = level.getEntity(checkpoint.entityId)
            entity !is Checkpoint
        }
        return checkpoints.lastOrNull()
    }

    /**
     * Must only be directly called on server due to setting game mode to adventure
     * @return if not already in GD mode
     */
    fun enterGD(mode: GDMode = GDMode.CUBE_3D, scale: Float? = 1F): Boolean {
        val noChange: Boolean = this.playingGD && this.mode == mode
        this.mode = mode
        if (scale != null) {
            this.scale = scale
        }

        val player = this.player
        if (noChange || player !is ServerPlayer) return false

        this.markDirty()
        return true
    }

    /**
     * @return if not already exited
     */
    fun exitGD(): Boolean {
        if (!this.playingGD) return false

        this.mode = null
        this.scale = 1F
        this.gdModeData = null
        this.checkpoints.clear()
        this.ignoreInput = false
        this.inputBuffer = false
        this.bufferLocked = false
        this.ringLocked = false
        this.dashRingID = ""

        val player = this.player
        player.pose = Pose.STANDING
        player.refreshDimensions()
        this.markDirty()
        return true
    }

    fun tick() {
        if (this.dirty) {
            (this.player as PlayerDuck).`geometryDash$updateSyncedGDData`()
            this.dirty = false
        }
        this.gdModeData?.tick()
        if (this.gdModeData != null) {
            player.pose = this.gdModeData!!.getPose()
        }
        player.refreshDimensions()
        this.markDirty()
    }

    fun markDirty() {
        this.dirty = true
    }

    fun save(compound: CompoundTag): CompoundTag {
        compound.putString(MODE_TAG, this.mode?.name ?: "")
        if (this.gdModeData != null) {
            this.gdModeData?.save(CompoundTag())?.let { compound.put(MODE_DATA_TAG, it) }
        }
        compound.putFloat(SCALE_TAG, this.scale)
        val checkpoints = ListTag()
        checkpoints.addAll(this.checkpoints.map { it.toTag() })
        compound.put(CHECKPOINTS_TAG, checkpoints)
        compound.putString(DASH_RING_TAG, this.dashRingID)
        return compound
    }

    fun load(compound: CompoundTag): CompoundTag {
        try {
            this.mode = GDMode.valueOf(compound.getString(MODE_TAG))
            if (compound.contains(MODE_DATA_TAG, Tag.TAG_COMPOUND.toInt())) {
                this.gdModeData?.load(compound.getCompound(MODE_DATA_TAG))
            } else {
                this.gdModeData = null
            }
        } catch (e: IllegalArgumentException) {
            this.mode = null
            this.gdModeData = null
        }

        this.scale = compound.getFloat(SCALE_TAG)
        this.checkpoints = compound.getList(CHECKPOINTS_TAG, CompoundTag.TAG_COMPOUND.toInt())
            .map { tag -> CheckpointSnapshot.fromTag(tag as CompoundTag) }
            .toMutableList()
        return compound
    }

    fun copyFrom(otherData: GDData) {
        this.mode = otherData.mode
        this.gdModeData = otherData.gdModeData
        this.scale = otherData.scale
        this.checkpoints = otherData.checkpoints
    }
}
