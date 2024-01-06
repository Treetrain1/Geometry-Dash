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
) {

    inline val playingGD: Boolean
        get() = this.mode != null

    private var prevGameType: GameType? = null

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

    fun syncS2C() {
        // TODO: add packet
    }

    @Environment(EnvType.CLIENT)
    fun syncC2S() {
        // TODO: add packet
    }
}
