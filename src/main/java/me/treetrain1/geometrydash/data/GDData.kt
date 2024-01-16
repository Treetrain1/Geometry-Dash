package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.data.mode.AbstractGDModeData
import me.treetrain1.geometrydash.entity.Checkpoint
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

@Suppress("MemberVisibilityCanBePrivate")

open class GDData @JvmOverloads constructor(
    @JvmField val player: Player,
    @JvmField var mode: GDMode? = null,
    @JvmField var gdModeData: AbstractGDModeData? = null,
    @JvmField var scale: Double = 1.0,
    @JvmField var checkpoints: MutableList<Int> = mutableListOf(),
    @JvmField var wasFallingBefore: Boolean = false,
    @JvmField var isInJump: Boolean = false
) {

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
        this.setMode(mode)
        this.scale = scale

        val player = this.player
        if (!alreadyEntered && player is ServerPlayer) {
            this.prevGameType = player.gameMode.gameModeForPlayer
            player.setGameMode(GameType.ADVENTURE)
        }
    }

    fun setMode(mode: GDMode) { //Should be called EVERY time the mode is set. This will ensure Mode Data works properly.
        this.mode = mode
        val modeDataSupplier = this.mode?.modeDataSupplier
        if (modeDataSupplier != null) {
            this.gdModeData = modeDataSupplier()
        }
    }

    fun exitGD() {
        val alreadyExited: Boolean = !this.playingGD
        this.mode = null
        this.scale = 1.0
        this.gdModeData = null
        this.checkpoints.clear()
        this.wasFallingBefore = false
        this.isInJump = false

        if (alreadyExited) return

        val player = this.player
        val prevType = this.prevGameType
        if (player is ServerPlayer && prevType != null) {
            player.setGameMode(prevType)
            this.prevGameType = null
        }
    }

    // TODO: Use + Test
    fun save(compound: CompoundTag) {
        compound.putString("mode", this.mode?.name ?: "")
        this.gdModeData?.save(compound)
        compound.putDouble("scale", this.scale)
        compound.putIntArray("checkpoints", this.checkpoints)
        compound.putBoolean("was_falling_before", this.wasFallingBefore)
        compound.putBoolean("is_in_jump", this.isInJump)
    }

    // TODO: Use + Test
    fun load(compound: CompoundTag) {
        try {
            this.setMode(GDMode.valueOf(compound.getString("mode")))
            if (this.mode != null) {
                val modeDataTag = compound.getCompound(this.mode!!.name)
                this.gdModeData?.load(modeDataTag)
            }
        } catch (e: IllegalArgumentException) {
            this.mode = null
        }

        this.scale = compound.getDouble("scale")
        this.checkpoints = compound.getIntArray("checkpoints").toMutableList()
        this.wasFallingBefore = compound.getBoolean("was_falling_before")
        this.isInJump = compound.getBoolean("is_in_jump")
    }

    fun syncS2C() {
        // TODO: add packet
    }

    @Environment(EnvType.CLIENT)
    fun syncC2S() {
        // TODO: add packet
    }

    fun tick() {
        val isFalling = this.player.fallDistance > 0.0
        val onGround = this.player.onGround()
        if (this.wasFallingBefore != isFalling) {
            if (!onGround) {
                this.gdModeData?.onFall()
                this.wasFallingBefore = isFalling
            } else {
                this.isInJump = false
                this.wasFallingBefore = false
            }
        }
        if (this.player.onGround()) this.isInJump = false
    }

}
