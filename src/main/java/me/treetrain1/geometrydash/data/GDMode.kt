package me.treetrain1.geometrydash.data

import net.minecraft.util.StringRepresentable

enum class GDMode : StringRepresentable {
    CUBE,
    SHIP,
    BALL,
    UFO,
    WAVE,
    ROBOT,
    SPIDER,
    SWING;

    override fun toString(): String = this.name

    override fun getSerializedName(): String = this.name
}
