@file:Suppress("NOTHING_TO_INLINE")

package me.treetrain1.geometrydash.util

import gravity_changer.api.GravityChangerAPI
import gravity_changer.command.LocalDirection
import gravity_changer.util.RotationUtil
import me.treetrain1.geometrydash.duck.PlayerDuck
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext

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
        this.teleportTo(this.x, 1000.0, this.z)
        this.kill()
    } else {
        val rayPos = raycast.location
        this.teleportToWithTicket(rayPos.x, rayPos.y, rayPos.z)
        this.setOnGround(true)
    }
}

/**
 * @return if colliding with a specific block
 */
fun Entity.isCollidingWithBlock(level: Level, pos: BlockPos): Boolean {
    if (this.isRemoved) return false

    val state = level.getBlockState(pos)
    val blockShape = state.getShape(level, pos, CollisionContext.of(this))
    val movedBlockShape = blockShape.move(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()).toAabbs()
    val expandedPlayerBoundingBox = this.boundingBox.inflate(0.075)

   for (aabb in movedBlockShape) {
       if (expandedPlayerBoundingBox.intersects(aabb)) return true
   }

    return false
}

// 0.42 is the player jump power
private const val defaultLaunch = 0.42 * 1.5

fun Player.launch(multiplier: Double) {
    val vec3: Vec3 = this.deltaMovement
    this.setDeltaMovement(vec3.x, defaultLaunch * multiplier, vec3.z)
    if (this.isSprinting) {
        val rot: Float = this.yRot * (Math.PI / 180.0).toFloat()
        this.deltaMovement = this.deltaMovement.add((-Mth.sin(rot) * 0.2f).toDouble(), 0.0, (Mth.cos(rot) * 0.2f).toDouble())
    }

    this.hasImpulse = true
}

// Minecraft accessors

@Environment(EnvType.CLIENT)
fun input(): Input? = Minecraft.getInstance().player?.input

inline val Player.gdData get() = (this as PlayerDuck).`geometryDash$getGDData`()

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
