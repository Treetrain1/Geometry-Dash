package me.treetrain1.geometrydash.util

import net.minecraft.FileUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.phys.Vec3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.io.path.Path

@PublishedApi
internal const val MOD_ID = "geometry_dash"

@PublishedApi
internal const val MOD_NAME = "Geometry Dash"

@JvmField
internal val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)!!

@JvmField
val MUSIC_DIRECTORY = Path("./.$MOD_ID/music/").apply {
    FileUtil.createDirectoriesSafe(this)
}

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
val DIMENSION_SPAWN = Vec3(25.5, 21.0, 959.5)

const val DEFAULT_GRAVITY_STRENGTH = 1.0

@JvmField
val DEFAULT_GRAVITY_DIRECTION = Direction.DOWN

@JvmField
val DEFAULT_GRAVITY = Vec3(0.0, 1.0, 0.0)

@JvmField
val UP_GRAVITY = Vec3(0.0, -1.0, 0.0)

@JvmField
val SOUTH_GRAVITY = Vec3(1.0, 0.0, 0.0)

@JvmField
val NORTH_GRAVITY = Vec3(-1.0, 0.0, 0.0)

@JvmField
val WEST_GRAVITY = Vec3(0.0, 0.0, 1.0)

@JvmField
val EAST_GRAVITY = Vec3(0.0, 0.0, -1.0)
