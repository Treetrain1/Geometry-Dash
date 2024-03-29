@file:Suppress("NOTHING_TO_INLINE")

package me.treetrain1.geometrydash.util

import me.treetrain1.geometrydash.duck.EntityDuck
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.Orb
import me.treetrain1.geometrydash.network.C2SFailPacket
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import org.joml.Vector3d

// GRAVITY

/**
 * Teleports the entity downward
 */
fun LivingEntity.vertTeleport(level: Level) {
    if (!level.isClientSide) return

    val rayOffset: Vec3 = this.toRelative(Vec3(0.0, -100.0, 0.0))
    val up = rayOffset.y > 0
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
        this.moveTo(this.x, 1000.0, this.z)
        this.health = 0.0F
        ClientPlayNetworking.send(C2SFailPacket())
    } else {
        val rayPos = raycast.location.add(0.0, if (up) -1.8 else 0.0, 0.0)
        this.moveTo(rayPos.x, rayPos.y, rayPos.z)
        this.setOnGround(true)
    }
}

/**
 * @return if colliding with a specific block's shape
 */
fun Entity.isCollidingWithBlockShape(level: Level, pos: BlockPos): Boolean {
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
private const val defaultLaunch = 0.42 * 2.38//1.53//1.8

fun LivingEntity.launch(multiplier: Double) {
    val vec3: Vec3 = this.deltaMovement
    val launchVal = defaultLaunch * multiplier
    val launchVec = this.toRelative(0.0, launchVal, 0.0)
    var newDelta: Vec3 = vec3

    if (launchVec.x != 0.0) newDelta = Vec3(launchVec.x, newDelta.y, newDelta.z)
    if (launchVec.y != 0.0) newDelta = Vec3(newDelta.x, launchVec.y, newDelta.z)
    if (launchVec.z != 0.0) newDelta = Vec3(newDelta.x, newDelta.y, launchVec.z)

    this.deltaMovement = newDelta
    this.hasImpulse = true
}

fun Player.dash(orb: Orb) {
    val data = this.gdData
    data.dashOrbID = orb.stringUUID

    this.deltaMovement = orb.forward
}

// these are for respecting gravity when setting movement
inline fun LivingEntity.setRelativeDelta(x: Double, y: Double, z: Double) {
    this.setRelativeDelta(Vec3(x, y, z))
}
inline fun LivingEntity.setRelativeDelta(vec: Vec3) {
    this.deltaMovement = this.toRelative(vec)
}
inline fun Entity.toRelative(x: Double, y: Double, z: Double) = this.toRelative(Vec3(x,y, z))
inline fun Entity.toRelative(vec: Vec3): Vec3 {
    return when (this.gravityDirection) {
        Direction.DOWN -> vec
        Direction.UP -> vec.multiply(1.0, -1.0, 1.0)
        Direction.NORTH -> Vec3(vec.y, vec.x, vec.z)
        Direction.SOUTH -> Vec3(vec.y * -1, vec.x, vec.z)
        Direction.EAST -> Vec3(vec.x, vec.z, vec.y)
        Direction.WEST -> Vec3(vec.x, vec.z, vec.y * -1)
    }
}

inline fun Vec3.toVector3d(): Vector3d = Vector3d(this.x, this.y, this.z)
inline fun Vector3d.toVec3(): Vec3 = Vec3(this.x, this.y, this.z)

// Minecraft accessors

@Environment(EnvType.CLIENT)
inline fun input(): Input? = Minecraft.getInstance().player?.input

inline val Player.gdData get() = (this as PlayerDuck).`geometryDash$getGDData`()

inline val Entity.gravity: Vec3
    get() {
        val strength = this.gravityStrength
        val direction = this.gravityDirection
        val vec = when (direction) {
            Direction.DOWN -> DEFAULT_GRAVITY
            Direction.UP -> UP_GRAVITY
            Direction.NORTH -> NORTH_GRAVITY
            Direction.SOUTH -> SOUTH_GRAVITY
            Direction.EAST -> EAST_GRAVITY
            Direction.WEST -> WEST_GRAVITY
        }
        return vec.scale(strength)
    }

@Suppress("USELESS_ELVIS")
inline var Entity.gravityStrength: Double
    get() = (this as EntityDuck).`geometryDash$getGravityStrength`() ?: DEFAULT_GRAVITY_STRENGTH
    set(value) = (this as EntityDuck).`geometryDash$setGravityStrength`(value)

@Suppress("USELESS_ELVIS")
inline var Entity.gravityDirection: Direction
    get() = (this as EntityDuck).`geometryDash$getGravityDirection`() ?: DEFAULT_GRAVITY_DIRECTION
    set(value) = (this as EntityDuck).`geometryDash$setGravityDirection`(value)

inline fun CompoundTag.putVec(key: String, vec: Vec3) {
    val list = ListTag()

    list.add(DoubleTag.valueOf(vec.x))
    list.add(DoubleTag.valueOf(vec.y))
    list.add(DoubleTag.valueOf(vec.z))

    this.put(key, list)
}

inline fun CompoundTag.getVec(key: String): Vec3 {
    val list = this.getList(key, CompoundTag.TAG_DOUBLE.toInt())

    return Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2))
}

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
