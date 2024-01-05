package me.treetrain1.geometrydash.data

open class GDData(
    @JvmField val player: Player,
    @JvmField var mode: GDMode? = null,
    @JvmField var scale: Double = 1.0,
) {

    inline val playingGD: Boolean
        get() = this.mode != null

    fun toggleGD() {
        if (this.playingGD)
            this.exitGD()
        else
            this.enterGD()
    }

    fun enterGD() {
        this.mode = GDMode.CUBE
    }

    fun exitGD() {
        this.mode = null
        this.scale = 1.0
    }

    fun syncS2C() {
        // TODO: add packet
    }

    @Environment(EnvType.CLIENT)
    fun syncC2S() {
        // TODO: add packet
    }
}