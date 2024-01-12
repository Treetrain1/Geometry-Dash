package me.treetrain1.geometrydash.data

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType

open class GDData(
    @JvmField val player: Player,
    @JvmField var mode: GDMode? = null,
    @JvmField var scale: Double = 1.0,
    @JvmField var checkpoints: MutableList<Int> = mutableListOf()
) {

    inline val playingGD: Boolean
        get() = this.mode != null

    private var prevGameType: GameType? = null

    private inline val level(): Level get() = this.player.level()

    inline val lastValidCheckpoint: Vec3? get() {
        val level = this.level
        checkpoints.removeAll { id ->
            val entity: Entity? = level.getEntity(id)
            entity !is Checkpoint
        }
        for (id in checkpoints.reverse()) {
            val entity: Entity? = level.getEntity(id)
            if (entity == null) continue
            return entity.position()
        }
        return null
    }

    fun updateCheckpoint() {
        val lastValid: Vec3 = this.lastValidCheckpoint
        // TODO: set player spawn to last checkpoint
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
        compound.putDouble("scale", this.scale)
        compound.putIntArray("checkpoints", this.checkpoints.toTypedArray())
    }

    // TODO: Use + Test
    fun load(compound: CompoundTag) {
        this.scale = compound.getDouble("scale")
        this.checkpoints = compound.getIntArray("checkpoints").toList()
    }

    fun syncS2C() {
        // TODO: add packet
    }

    @Environment(EnvType.CLIENT)
    fun syncC2S() {
        // TODO: add packet
    }
}
