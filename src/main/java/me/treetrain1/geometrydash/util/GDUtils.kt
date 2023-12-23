package me.treetrain1.geometrydash.util

import gravity_changer.api.GravityChangerAPI
import gravity_changer.command.LocalDirection
import gravity_changer.util.RotationUtil
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

// GRAVITY

fun setRelative(entity: Entity, direction: LocalDirection) {
    val gravityDirection = GravityChangerAPI.getGravityDirection(entity)
    val combinedRelativeDirection = when (direction) {
        LocalDirection.DOWN -> Direction.DOWN
        LocalDirection.UP -> Direction.UP
        LocalDirection.FORWARD, LocalDirection.BACKWARD, LocalDirection.LEFT, LocalDirection.RIGHT -> Direction.from2DDataValue(
            direction.horizontalOffset + Direction.fromYRot(entity.yRot.toDouble()).get2DDataValue()
        )
    }
    val newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection)
    GravityChangerAPI.setBaseGravityDirection(entity, newGravityDirection)
}

// LOGGING
@JvmOverloads
fun log(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) {
        LOGGER?.info(string)
    }
}

@JvmOverloads
fun logMod(string: String, shouldLog: Boolean = true) {
    if (shouldLog) {
        LOGGER?.info("$string $MOD_ID")
    }
}

@JvmOverloads
fun logDebug(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER?.debug(string)
}

@JvmOverloads
fun logWarn(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER?.warn(string)
}

@JvmOverloads
fun logError(string: String?, error: Throwable? = null, shouldLog: Boolean = true) {
    if (shouldLog) {
        LOGGER?.error(string, error)
    }
}

// IDENTIFIERS

fun id(path: String): ResourceLocation {
    return ResourceLocation(MOD_ID, path)
}

fun vanillaId(path: String): ResourceLocation {
    return ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, path)
}

fun string(path: String): String {
    return id(path).toString()
}
