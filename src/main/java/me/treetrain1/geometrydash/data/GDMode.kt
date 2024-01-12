package me.treetrain1.geometrydash.data

enum class GDMode(val name: String) : StringRepresentable {
    CUBE("cube"),
    SHIP("ship"),
    BALL("ball"),
    UFO("ufo"),
    WAVE("wave"),
    ROBOT("robot"),
    SPIDER("spider"),
    SWING("swing")

    override fun toString(): String = this.name

    override fun getSerializedName(): String = this.name
}
