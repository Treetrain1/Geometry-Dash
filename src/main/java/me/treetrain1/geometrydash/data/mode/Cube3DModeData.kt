package me.treetrain1.geometrydash.data.mode

class Cube3DModeData : CubeModeData() {

    override fun useGDCamera(): Boolean {
        return false
    }

    override fun lockCamera(): Boolean {
        return false
    }
}
