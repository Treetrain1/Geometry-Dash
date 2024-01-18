package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.GDModeData
import me.treetrain1.geometrydash.entity.Checkpoint
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

@Suppress("MemberVisibilityCanBePrivate")
open class GDData @JvmOverloads constructor(
    @JvmField val player: Player,
    @JvmField var gdModeData: GDModeData? = null,
    @JvmField var scale: Double = 1.0,
    @JvmField var checkpoints: MutableList<Int> = mutableListOf(),
) {

    var mode: GDMode? = null
        set(value) {
            field = value
            val modeDataSupplier = value?.modeDataSupplier
            if (modeDataSupplier != null) {
                val modeData = modeDataSupplier()
                modeData.gdData = this
                this.gdModeData = modeData
            }
        }

    inline val playingGD: Boolean
        get() = this.mode != null

    private var prevGameType: GameType? = null

    @PublishedApi
    internal inline val level: Level get() = this.player.level()

    inline val lastValidCheckpoint: BlockPos? get() {
        val level = this.level
        checkpoints.removeAll { id ->
            val entity: Entity? = level.getEntity(id)
            entity !is Checkpoint
        }
        for (id in checkpoints.reversed()) {
            val entity: Entity = level.getEntity(id) ?: continue
            return entity.blockPosition()
        }
        return null
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

    fun setGD(mode: GDMode?, scale: Double = 1.0) {
        if (mode == null) {
            this.exitGD()
        } else {
            this.enterGD(mode, scale)
        }
    }

    fun enterGD(mode: GDMode = GDMode.CUBE, scale: Double = 1.0) {
        val alreadyEntered: Boolean = this.playingGD
        this.mode = mode
        this.scale = scale

        val player = this.player
        if (!alreadyEntered && player is ServerPlayer) {
            this.prevGameType = player.gameMode.gameModeForPlayer
            player.setGameMode(GameType.ADVENTURE)
        }
    }

    fun exitGD() {
        val alreadyExited: Boolean = !this.playingGD
        this.mode = null
        this.scale = 1.0
        this.gdModeData = null
        this.checkpoints.clear()

        if (alreadyExited) return

        val player = this.player
        val prevType = this.prevGameType
        if (player is ServerPlayer && prevType != null) {
            player.setGameMode(prevType)
            this.prevGameType = null
        }
        player.pose = Pose.STANDING
        player.refreshDimensions()
    }

    fun tick() {
        this.gdModeData?.tick()
        if (this.gdModeData != null) {
            player.pose = this.gdModeData!!.getPose()
        }
        player.refreshDimensions()
    }

    // TODO: Use + Test
    fun save(compound: CompoundTag) {
        compound.putString("mode", this.mode?.name ?: "")
        val dataTag = CompoundTag()
        this.gdModeData?.save(dataTag)
        compound.put("mode_data", dataTag)
        compound.putDouble("scale", this.scale)
        compound.putIntArray("checkpoints", this.checkpoints)
    }

    // TODO: Use + Test
    fun load(compound: CompoundTag) {
        try {
            val newMode = GDMode.valueOf(compound.getString("mode"))
            this.mode = newMode
            val modeDataTag = compound.getCompound(newMode.name + "_data")
            this.gdModeData?.load(modeDataTag)
        } catch (e: IllegalArgumentException) {
            this.mode = null
        }

        this.scale = compound.getDouble("scale")
        this.checkpoints = compound.getIntArray("checkpoints").toMutableList()
    }

    fun syncS2C() {
        // TODO: add packet
    }

    @Environment(EnvType.CLIENT)
    fun syncC2S() {
        // TODO: add packet
    }

}
