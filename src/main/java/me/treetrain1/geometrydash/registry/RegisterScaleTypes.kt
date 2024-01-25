package me.treetrain1.geometrydash.registry

import virtuoel.pehkui.api.ScaleEasings
import virtuoel.pehkui.api.ScaleModifiers
import virtuoel.pehkui.api.ScaleType

object RegisterScaleTypes {

    @JvmField
    val DASH: ScaleType = register(
        "dash",
        null,
        ScaleModifiers.BASE_MULTIPLIER
    ).apply {
        this.defaultEasing = ScaleEasings.EXPONENTIAL_IN_OUT
    }
}
