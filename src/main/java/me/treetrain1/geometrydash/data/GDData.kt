package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.data.mode.getGDModeData
import me.treetrain1.geometrydash.data.mode.putGDModeData
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.getVec
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.putVec
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
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Suppress("MemberVisibilityCanBePrivate")
open class GDData(
    modeData: GDModeData? = null,
    @JvmField
    var checkpoints: MutableList<CheckpointSnapshot> = mutableListOf(),
    @JvmField
    var cameraData: CameraData = CameraData(),
    @JvmField
    var isVisible: Boolean = true,
    @JvmField
    var timeMod: Float = 1F,
    @JvmField
    protected var prevGameType: GameType? = null,
    @JvmField
    protected var prevGravity: Vec3? = null,
) {

    var modeData: GDModeData? = modeData?.also { it.gdData = this }
        set(value) {
            value?.gdData = this
            field = value
        }

    constructor(
        mode: GDMode?,
        checkpoints: MutableList<CheckpointSnapshot> = mutableListOf(),
        cameraData: CameraData = CameraData(),
        isVisible: Boolean = true,
        timeMod: Float = 1F,
        prevGameType: GameType? = null,
        prevGravity: Vec3? = null,
    ) : this(
        mode?.modeData?.invoke(),
        checkpoints,
        cameraData,
        isVisible,
        timeMod,
        prevGameType,
        prevGravity
    )

    constructor(player: Player) : this() {
        this.player = player
    }

    inline var mode: GDMode?
        get() = this.modeData?.mode
        set(value) {
            val modeData = value?.modeData?.invoke()
            modeData?.gdData = this
            this.modeData = modeData
        }

    companion object {
        private const val MODE_TAG = "Mode"
        private const val MODE_DATA_TAG = "ModeData"
        private const val SCALE_TAG = "Scale"
        private const val CHECKPOINTS_TAG = "Checkpoints"
        private const val PREV_GAME_TYPE_TAG = "PrevGameType"
        private const val PREV_GRAVITY_TAG = "PrevGravity"
        private const val DASH_RING_TAG = "DashRingID"
        private const val CAMERA_MIRROR_PROGRESS_TAG = "CameraMirrorProgress"

        @JvmField
        val GD_DATA: EntityDataAccessor<in CompoundTag> = SynchedEntityData.defineId(
            Player::class.java, EntityDataSerializers.COMPOUND_TAG
        )

        @JvmField
        val CODEC: Codec<GDData> = RecordCodecBuilder.create { instance ->
            instance.group(
                GDModeData.CODEC.optionalFieldOf("mode_data").forGetter { Optional.ofNullable(it.modeData) },
                CheckpointSnapshot.CODEC.listOf().fieldOf("checkpoints").forGetter(GDData::checkpoints),
                CameraData.CODEC.fieldOf("camera_data").forGetter(GDData::cameraData),
                Codec.BOOL.fieldOf("isVisible").forGetter(GDData::isVisible),
                Codec.FLOAT.fieldOf("time_multiplier").forGetter(GDData::timeMod),
                GameType.CODEC.optionalFieldOf("previous_game_type").forGetter { Optional.ofNullable(it.prevGameType) },
                Vec3.CODEC.optionalFieldOf("previous_gravity").forGetter { Optional.ofNullable(it.prevGravity) },
            ).apply(instance) { modeData, checkpoints, cameraData, isVisible, timeMod, prevGameType, prevGravity ->
                GDData(modeData.getOrNull(), checkpoints, cameraData, isVisible, timeMod, prevGameType.getOrNull(), prevGravity.getOrNull())
            }
        }
    }

    lateinit var player: Player

    inline var scale: Float
        get() = ScaleTypes.WIDTH.getScaleData(this.player).scale
        set(value) {
            val width = ScaleTypes.WIDTH.getScaleData(this.player)
            val height = ScaleTypes.HEIGHT.getScaleData(this.player)
            width.targetScale = value
            height.targetScale = value
        }

    @JvmField
    var dirty: Boolean = true

    inline val playingGD: Boolean
        get() = this.modeData != null

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

    @JvmField
    var cameraMirrorProgress: Float = 1F

    @JvmField
    var cameraMirrorDirection: MirrorDirection? = null

    fun mirrorCamera() {
        cameraMirrorDirection = if (cameraMirrorProgress > 0) {
            MirrorDirection.LEFT
        } else MirrorDirection.RIGHT
    }

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

    fun toggleGD() {
        if (this.playingGD)
            this.exitGD()
        else
            this.enterGD()
    }

    fun setGD(value: Boolean) {
        if (value == this.playingGD) return

        toggleGD()
    }

    fun setGD(mode: GDMode?, scale: Float? = 1F): Boolean {
        if (mode == null) {
            return this.exitGD()
        }
        return this.enterGD(mode, scale)
    }

    /**
     * Must only be directly called on server due to setting game mode to adventure
     * @return if not already in GD mode
     */
    fun enterGD(mode: GDMode = GDMode.CUBE, scale: Float? = 1F): Boolean {
        val noChange: Boolean = this.playingGD && this.mode == mode
        this.mode = mode
        if (scale != null) {
            this.scale = scale
        }

        val player = this.player
        if (noChange || player !is ServerPlayer) return false

        if (this.prevGameType == null)
            this.prevGameType = player.gameMode.gameModeForPlayer
        player.setGameMode(GameType.ADVENTURE)

        if (this.prevGravity == null)
            this.prevGravity = player.gravity
        player.gravity = Vec3(0.0, 1.0, 0.0)

        this.syncData()
        return true
    }

    /**
     * @return if not already exited
     */
    fun exitGD(): Boolean {
        if (!this.playingGD) return false

        this.modeData = null
        this.scale = 1F
        this.modeData = null
        this.checkpoints.clear()
        this.ignoreInput = false
        this.inputBuffer = false
        this.bufferLocked = false
        this.ringLocked = false
        this.dashRingID = ""
        this.cameraMirrorProgress = 1F

        val player = this.player
        val prevType = this.prevGameType
        if (player is ServerPlayer && prevType != null) {
            player.setGameMode(prevType)
            this.prevGameType = null
        }
        val prevGravity = this.prevGravity
        if (prevGravity != null) {
            player.gravity = prevGravity
            this.prevGravity = null
        }
        player.pose = Pose.STANDING
        player.refreshDimensions()
        this.syncData()
        return true
    }

    fun tick() {
        val player = this.player
        if (this.dirty) {
            if (player.level().isClientSide)
                this.syncData()
            this.dirty = false
        }
        this.modeData?.tick()
        if (this.modeData != null) {
            player.pose = this.modeData!!.getPose()
        }
        when (this.cameraMirrorDirection) {
            MirrorDirection.LEFT -> {
                this.cameraMirrorProgress -= 0.08F
                if (this.cameraMirrorProgress <= -1F) {
                    this.cameraMirrorProgress = -1F
                    this.cameraMirrorDirection = null
                }
            }
            MirrorDirection.RIGHT -> {
                this.cameraMirrorProgress += 0.08F
                if (this.cameraMirrorProgress >= 1F) {
                    this.cameraMirrorProgress = 1F
                    this.cameraMirrorDirection = null
                }
            }
            else -> {}
        }
        player.refreshDimensions()
        if (player.level().isClientSide)
            this.markDirty()
    }

    fun markDirty() {
        this.dirty = true
    }

    fun save(compound: CompoundTag): CompoundTag {
        compound.putGDModeData(MODE_DATA_TAG, this.modeData)
        compound.putFloat(SCALE_TAG, this.scale)
        val checkpoints = ListTag()
        checkpoints.addAll(this.checkpoints.map { it.toTag() })
        compound.put(CHECKPOINTS_TAG, checkpoints)
        compound.putInt(PREV_GAME_TYPE_TAG, this.prevGameType?.id ?: -1)
        compound.putVec(PREV_GRAVITY_TAG, this.prevGravity ?: Vec3(0.0, 1.0, 0.0))
        compound.putString(DASH_RING_TAG, this.dashRingID)
        compound.putFloat(CAMERA_MIRROR_PROGRESS_TAG, this.cameraMirrorProgress)
        return compound
    }

    fun load(compound: CompoundTag): CompoundTag {
        if (compound.contains(MODE_DATA_TAG, Tag.TAG_COMPOUND.toInt())) {
            this.modeData = compound.getGDModeData(MODE_DATA_TAG)
        } else {
            this.modeData = null
        }

        this.scale = compound.getFloat(SCALE_TAG)
        this.checkpoints = compound.getList(CHECKPOINTS_TAG, CompoundTag.TAG_COMPOUND.toInt())
            .map { tag -> CheckpointSnapshot.fromTag(tag as CompoundTag) }
            .toMutableList()
        this.prevGameType = GameType.byNullableId(compound.getInt(PREV_GAME_TYPE_TAG))
        this.prevGravity = compound.getVec(PREV_GRAVITY_TAG)
        this.cameraMirrorProgress = compound.getFloat(CAMERA_MIRROR_PROGRESS_TAG)
        return compound
    }

    fun copyFrom(otherData: GDData) {
        this.modeData = otherData.modeData
        this.scale = otherData.scale
        this.checkpoints = otherData.checkpoints
        this.prevGameType = otherData.prevGameType
        this.prevGravity = otherData.prevGravity
        this.dashRingID = otherData.dashRingID
        this.cameraMirrorProgress = otherData.cameraMirrorProgress

        this.syncData()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun syncData() {
        (this.player as? PlayerDuck)?.`geometryDash$updateSyncedGDData`()
    }

    enum class MirrorDirection {
        LEFT,
        RIGHT;
    }
}
