package me.treetrain1.geometrydash.data

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
        if (value != this.playingGD) return

        toggleGD()
    }

    fun enterGD() {
        this.mode = GDMode.CUBE

        val player = this.player
        if (player is ServerPlayer) {
            this.prevGameType = player.gameMode.gameModeForPlayer
            player.setGameMode(GameType.ADVENTURE)
        }
    }

    fun exitGD() {
        this.mode = null
        this.scale = 1.0

        val player = this.player
        val prevType = this.prevGameType
        if (player is ServerPlayer && prevType != null) {
            player.setGameMode(this.prevGameType)
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