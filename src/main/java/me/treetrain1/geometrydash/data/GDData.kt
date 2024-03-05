package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import de.keksuccino.melody.resources.audio.SimpleAudioFactory
import me.treetrain1.geometrydash.data.GDData.MirrorDirection.Companion.getMirrorDirection
import me.treetrain1.geometrydash.data.GDData.MirrorDirection.Companion.putMirrorDirection
import me.treetrain1.geometrydash.data.SongSource.Companion.getSongSource
import me.treetrain1.geometrydash.data.SongSource.Companion.putSongSource
import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.data.mode.getGDModeData
import me.treetrain1.geometrydash.data.mode.putGDModeData
import me.treetrain1.geometrydash.duck.GDClip
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL11
import virtuoel.pehkui.api.ScaleTypes
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Suppress("MemberVisibilityCanBePrivate")
open class GDData(
    modeData: GDModeData? = null,
    @JvmField
    var song: SongSource? = null,
    @JvmField
    var checkpoints: MutableList<CheckpointSnapshot> = mutableListOf(),
    @JvmField
    var cameraData: CameraData = CameraData(),
    @JvmField
    var isVisible: Boolean = true,
    @JvmField
    var timeMod: Float = 1F,
    @JvmField
    var dashOrbID: String = "",
    @JvmField
    var cameraMirrorProgress: Float = 1F,
    @JvmField
    var cameraMirrorDirection: MirrorDirection? = null,
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
        song: SongSource? = null,
        checkpoints: MutableList<CheckpointSnapshot> = mutableListOf(),
        cameraData: CameraData = CameraData(),
        isVisible: Boolean = true,
        timeMod: Float = 1F,
        dashOrbID: String = "",
        cameraMirrorProgress: Float = 1F,
        cameraMirrorDirection: MirrorDirection? = null,
        prevGameType: GameType? = null,
        prevGravity: Vec3? = null,
    ) : this(
        mode?.modeData?.invoke(),
        song,
        checkpoints,
        cameraData,
        isVisible,
        timeMod,
        dashOrbID,
        cameraMirrorProgress,
        cameraMirrorDirection,
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
        private const val MODE_DATA_TAG = "ModeData"
        private const val SONG_TAG = "Song"
        private const val CHECKPOINTS_TAG = "Checkpoints"
        private const val CAMERA_DATA_TAG = "CameraData"
        private const val IS_VISIBLE_TAG = "IsVisible"
        private const val SCALE_TAG = "Scale"
        private const val TIME_MULTIPLIER_TAG = "TimeMultiplier"
        private const val DASH_ORB_ID = "DashOrbID"
        private const val CAMERA_MIRROR_PROGRESS_TAG = "CameraMirrorProgress"
        private const val CAMERA_MIRROR_DIRECTION_TAG = "CameraMirrorDirection"
        private const val PREV_GAME_TYPE_TAG = "PrevGameType"
        private const val PREV_GRAVITY_TAG = "PrevGravity"

        @JvmField
        val GD_DATA: EntityDataAccessor<in CompoundTag> = SynchedEntityData.defineId(
            Player::class.java, EntityDataSerializers.COMPOUND_TAG
        )

        @JvmField
        val CODEC: Codec<GDData> = RecordCodecBuilder.create { instance ->
            instance.group(
                GDModeData.CODEC.optionalFieldOf(MODE_DATA_TAG).forGetter { Optional.ofNullable(it.modeData) },
                SongSource.CODEC.optionalFieldOf(SONG_TAG).forGetter { Optional.ofNullable(it.song) },
                CheckpointSnapshot.CODEC.listOf().fieldOf(CHECKPOINTS_TAG).forGetter(GDData::checkpoints),
                CameraData.CODEC.fieldOf(CAMERA_DATA_TAG).forGetter(GDData::cameraData),
                Codec.BOOL.fieldOf(IS_VISIBLE_TAG).forGetter(GDData::isVisible),
                Codec.FLOAT.fieldOf(TIME_MULTIPLIER_TAG).forGetter(GDData::timeMod),
                Codec.STRING.fieldOf(DASH_ORB_ID).forGetter(GDData::dashOrbID),
                Codec.FLOAT.fieldOf(CAMERA_MIRROR_PROGRESS_TAG).forGetter(GDData::cameraMirrorProgress),
                MirrorDirection.CODEC.fieldOf(CAMERA_MIRROR_DIRECTION_TAG).forGetter(GDData::cameraMirrorDirection),
                GameType.CODEC.optionalFieldOf(PREV_GAME_TYPE_TAG).forGetter { Optional.ofNullable(it.prevGameType) },
                Vec3.CODEC.optionalFieldOf(PREV_GRAVITY_TAG).forGetter { Optional.ofNullable(it.prevGravity) },
            ).apply(instance) { modeData, song, checkpoints, cameraData, isVisible, timeMod, dashOrbID, cameraMirrorProgress, cameraMirrorDirection, prevGameType, prevGravity ->
                GDData(modeData.getOrNull(), song.getOrNull(), checkpoints, cameraData, isVisible, timeMod, dashOrbID, cameraMirrorProgress, cameraMirrorDirection, prevGameType.getOrNull(), prevGravity.getOrNull())
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
     * Whether or not an input release is required for Orb interaction
     */
    @Environment(EnvType.CLIENT)
    @JvmField
    var orbLocked: Boolean = false

    inline val isDashing: Boolean get() = dashOrbID.isNotEmpty()

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

    inline val canBounceFromOrb: Boolean get() = !(ignoreInput || orbLocked || isDashing) && inputBuffer

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
        player.gravity = DEFAULT_GRAVITY

        this.syncData()
        return true
    }

    /**
     * @return if not already exited
     */
    fun exitGD(): Boolean {
        if (!this.playingGD) return false

        this.modeData = null
        this.song = null
        this.scale = 1F
        this.modeData = null
        this.checkpoints.clear()
        this.ignoreInput = false
        this.inputBuffer = false
        this.bufferLocked = false
        this.orbLocked = false
        this.dashOrbID = ""
        this.cameraMirrorProgress = 1F
        this.cameraMirrorDirection = null

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
        compound.putSongSource(SONG_TAG, this.song)
        compound.put(CAMERA_DATA_TAG, this.cameraData.toTag())
        compound.putBoolean(IS_VISIBLE_TAG, this.isVisible)
        compound.putFloat(TIME_MULTIPLIER_TAG, this.timeMod)
        compound.putString(DASH_ORB_ID, this.dashOrbID)
        compound.putFloat(CAMERA_MIRROR_PROGRESS_TAG, this.cameraMirrorProgress)
        compound.putMirrorDirection(CAMERA_MIRROR_DIRECTION_TAG, this.cameraMirrorDirection)
        compound.putInt(PREV_GAME_TYPE_TAG, this.prevGameType?.id ?: -1)
        compound.putVec(PREV_GRAVITY_TAG, this.prevGravity ?: DEFAULT_GRAVITY)
        return compound
    }

    fun load(compound: CompoundTag): CompoundTag {
        if (compound.contains(MODE_DATA_TAG, Tag.TAG_COMPOUND.toInt())) {
            this.modeData = compound.getGDModeData(MODE_DATA_TAG)
        } else {
            this.modeData = null
        }

        this.song = compound.getSongSource(SONG_TAG)
        this.scale = compound.getFloat(SCALE_TAG)
        this.checkpoints = compound.getList(CHECKPOINTS_TAG, CompoundTag.TAG_COMPOUND.toInt())
            .map { tag -> CheckpointSnapshot.fromTag(tag as CompoundTag) }
            .toMutableList()
        this.cameraData = CameraData.fromTag(compound.getCompound(CAMERA_DATA_TAG))
        this.isVisible = compound.getBoolean(IS_VISIBLE_TAG)
        this.timeMod = compound.getFloat(TIME_MULTIPLIER_TAG)
        this.dashOrbID = compound.getString(DASH_ORB_ID)
        this.cameraMirrorProgress = compound.getFloat(CAMERA_MIRROR_PROGRESS_TAG)
        this.cameraMirrorDirection = compound.getMirrorDirection(CAMERA_MIRROR_DIRECTION_TAG)
        this.prevGameType = GameType.byNullableId(compound.getInt(PREV_GAME_TYPE_TAG))
        this.prevGravity = compound.getVec(PREV_GRAVITY_TAG)
        return compound
    }

    fun copyFrom(otherData: GDData) {
        this.modeData = otherData.modeData
        this.song = otherData.song
        this.scale = otherData.scale
        this.checkpoints = otherData.checkpoints
        this.cameraData = otherData.cameraData
        this.isVisible = otherData.isVisible
        this.timeMod = otherData.timeMod
        this.dashOrbID = otherData.dashOrbID
        this.cameraMirrorProgress = otherData.cameraMirrorProgress
        this.cameraMirrorDirection = otherData.cameraMirrorDirection
        this.prevGameType = otherData.prevGameType
        this.prevGravity = otherData.prevGravity

        this.syncData()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun syncData() {
        (this.player as? PlayerDuck)?.`geometryDash$updateSyncedGDData`()
    }

    enum class MirrorDirection : StringRepresentable {
        LEFT,
        RIGHT;

        companion object {
            @JvmField
            val CODEC: Codec<MirrorDirection> = StringRepresentable.fromEnum(::values)

            fun CompoundTag.getMirrorDirection(key: String): MirrorDirection? {
                if (!this.contains(key, CompoundTag.TAG_STRING.toInt()))
                    return null
                val str: String = this.getString(key)
                return try {
                    MirrorDirection.valueOf(str.uppercase())
                } catch (e: Exception) {
                    null
                }
            }

            fun CompoundTag.putMirrorDirection(key: String, direction: MirrorDirection?): CompoundTag {
                this.putString(key, direction?.serializedName ?: "")
                return this
            }
        }

        override fun getSerializedName(): String = this.name.lowercase()
    }
}
