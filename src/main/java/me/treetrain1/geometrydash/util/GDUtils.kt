package me.treetrain1.geometrydash.util

import gravity_changer.api.GravityChangerAPI
import gravity_changer.command.LocalDirection
import gravity_changer.util.RotationUtil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
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
 * Teleports the entity downward
 * WIP
 */
fun Entity.vertTeleport(level: Level) {
    /*
        TODO: make a raycast thing going down relative to the entity gravity
        (not up bc gravity is already flipped)
        teleport when it hits
        if it misses, tp to y 1000 & kill the entity
    */
    val gravDir = GravityChangerAPI.getGravityDirection(this)
    val rayOffset: Vec3 = RotationUtil.vecPlayerToWorld(0.0, 100.0, 0.0, gravDir)
    val rayEnd: Vec3 = this.position().add(rayOffset)
    val raycast: BlockHitResult = level.clip(
        ClipContext(
            this.position(),
            rayEnd,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            this
        )
    ) ?: return

    if (raycast.type == HitResult.Type.MISS) {
        // TODO: kill entity
    } else {
        val rayPos = raycast.location
        val endOffset = RotationUtil.vecPlayerToWorld(0.0, -0.00001, 0.0, gravDir)
        //val end = rayPos.add(endOffset)
        this.teleportToWithTicket(rayPos.x, rayPos.y, rayPos.z)
        this.setOnGround(true)
    }
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

// Minecraft accessors

@Environment(EnvType.CLIENT)
fun input(): Input? = Minecraft.getInstance().player?.input

// Kotlin stuff

inline fun <T> ifClient(crossinline runnable: () -> T): T?
    = if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) runnable() else null

inline fun ifClient(crossinline runnable: () -> Unit, crossinline elseRun: () -> Unit)
    = ifClient(runnable) ?: elseRun()

// LOGGING
@JvmOverloads
fun log(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER.info(string)
}

@JvmOverloads
fun logMod(string: String, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER.info("$string $MOD_ID")
}

@JvmOverloads
fun logDebug(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER.debug(string)
}

@JvmOverloads
fun logWarn(string: String?, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER.warn(string)
}

@JvmOverloads
fun logError(string: String?, error: Throwable? = null, shouldLog: Boolean = true) {
    if (shouldLog) LOGGER.error(string, error)
}

// IDENTIFIERS

inline fun id(path: String): ResourceLocation
    = ResourceLocation(MOD_ID, path)

inline fun vanillaId(path: String): ResourceLocation
    = ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, path)

inline fun string(path: String): String
    = id(path).toString()
