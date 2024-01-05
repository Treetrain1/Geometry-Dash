package me.treetrain1.geometrydash.item

import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext

open class PortalItem(props: Properties) : Item(props) {

    enum class PortalType(
        val modeSwitch: GDMode? = null,
        val flipGravity: Boolean = false,
        val scale: Double? = null,
    ) {
        CUBE(modeSwitch = GDMode.CUBE),
        ROBOT(modeSwitch = GDMode.ROBOT),
        SHIP(modeSwitch = GDMode.SHIP),
        WAVE(modeSwitch = GDMode.WAVE),
        SWING(modeSwitch = GDMode.SWING),

        GRAVITY_FLIP(flipGravity = true),

        SCALE_NORMAL(scale = 1.0),
        SCALE_SMALL(scale = 0.5),
        SCALE_LARGE(scale = 2.0),
    }
}