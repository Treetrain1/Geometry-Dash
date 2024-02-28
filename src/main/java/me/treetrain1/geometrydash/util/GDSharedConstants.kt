package me.treetrain1.geometrydash.util

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.phys.Vec3
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@PublishedApi
internal const val MOD_ID = "geometry_dash"

@PublishedApi
internal const val MOD_NAME = "Geometry Dash"

@JvmField
internal val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)!!

const val DATA_VERSION = 1

@JvmField
val GD_MOVEMENT_SPEED = AttributeModifier(
    "Geometry Dash speed boost",
    1.25,//1.25,
    AttributeModifier.Operation.MULTIPLY_TOTAL
)
const val GD_GRAVITY_PULL: Double = -0.1//-0.18

/**
 * The location where portals to the Geometry dimension will lead.
 */
@JvmField
val DIMENSION_SPAWN = Vec3(0.0, 10.0, 0.0)
