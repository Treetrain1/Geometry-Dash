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

const val GD_TICKS_PER_SECOND: Int = 60

const val GD_MOVEMENT_SPEED: Float = 1.75F
const val GD_GRAVITY_PULL: Double = -0.25//-0.18

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
