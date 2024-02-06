package me.treetrain1.geometrydash.util

import net.minecraft.world.entity.ai.attributes.AttributeModifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@PublishedApi
internal const val MOD_ID = "geometry_dash"

@PublishedApi
internal const val MOD_NAME = "Geometry Dash"

@JvmField
internal val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)!!

@JvmField
val GD_MOVEMENT_SPEED = AttributeModifier(
    "Geometry Dash speed boost",
    0.5,//1.25,
    AttributeModifier.Operation.MULTIPLY_TOTAL
)
const val GD_GRAVITY_PULL: Double = -0.1//-0.18
