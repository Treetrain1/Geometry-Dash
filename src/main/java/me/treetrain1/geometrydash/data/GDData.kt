package me.treetrain1.geometrydash.data

import me.treetrain1.geometrydash.entity.Checkpoint
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

@Suppress("MemberVisibilityCanBePrivate")

open class GDData @JvmOverloads constructor(
    @JvmField val player: Player,
    @JvmField var mode: GDMode? = null,
    @JvmField var scale: Double = 1.0,
    @JvmField var cubeRotation: Float = 0.0F,
    @JvmField var prevCubeRotation: Float = 0.0F,
    @JvmField var targetCubeRotation: Float = 0.0F,
    @JvmField var checkpoints: MutableList<Int> = mutableListOf()
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
        // TODO: how do you write an enum i forgot
        compound.putString("mode", this.mode?.name)
        compound.putDouble("scale", this.scale)
        compound.putIntArray("checkpoints", this.checkpoints)
    }

    // TODO: Use + Test
    fun load(compound: CompoundTag) {
        try {
            this.mode = GDMode.valueOf(compound.getString("mode"))
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

    fun incrementCubeRotation(isJumping: Boolean = true) {
        var additionalRotation = 180F
        if (!isJumping)
            additionalRotation = 90F;
        //how do i do the bl ? 180 : 90 stuff help me
        this.targetCubeRotation += additionalRotation
    }

    fun tick() {
        this.prevCubeRotation = this.cubeRotation
        this.cubeRotation += (this.targetCubeRotation - this.cubeRotation) * 0.25F
    }

    fun getCubeRotation(tickDelta: Float): Float {
        return Mth.lerp(tickDelta, this.prevCubeRotation, this.cubeRotation)
    }

}
