package me.treetrain1.geometrydash.util

import gravity_changer.api.GravityChangerAPI
import gravity_changer.command.LocalDirection
import gravity_changer.util.RotationUtil
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes

// GRAVITY

fun Entity.setRelative(direction: LocalDirection) {
    val gravityDirection = GravityChangerAPI.getGravityDirection(this)
    val combinedRelativeDirection = when (direction) {
        LocalDirection.DOWN -> Direction.DOWN
        LocalDirection.UP -> Direction.UP
        LocalDirection.FORWARD, LocalDirection.BACKWARD, LocalDirection.LEFT, LocalDirection.RIGHT -> Direction.from2DDataValue(
            direction.horizontalOffset + Direction.fromYRot(this.yRot.toDouble()).get2DDataValue()
        )
    }
    val newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection)
    GravityChangerAPI.setBaseGravityDirection(this, newGravityDirection)
}

/**
 * @return if colliding with a jump pad block
 */
fun Entity.isCollidingWithPad(level: Level, pos: BlockPos): Boolean {
    if (this.isRemoved) return false

    val state = level.getBlockState(pos)
    val shape = state.getShape(level, pos, CollisionContext.of(this))
    val shape2 = shape.move(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
    return Shapes.joinIsNotEmpty(shape2, Shapes.create(this.boundingBox), BooleanOp.AND)
}

// Kotlin stuff

inline fun <T> ifClient(crossinline runnable: () -> T): T?
    = if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) runnable() else null

inline fun ifClient(crossinline runnable: () -> Unit, crossinline elseRun: () -> Unit)
    = ifClient(runnable) ?: elseRun()

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
