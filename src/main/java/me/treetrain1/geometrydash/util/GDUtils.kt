@file:Suppress("NOTHING_TO_INLINE")

package me.treetrain1.geometrydash.util

import me.treetrain1.geometrydash.duck.EntityDuck
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.network.C2SFailPacket
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext

// GRAVITY

inline fun Entity.setRelative(flip: Boolean) {
    if (flip) this.gravity *= -1
}

/**
 * Teleports the entity downward
 */
fun LivingEntity.vertTeleport(level: Level) {
    if (!level.isClientSide) return

    val gravity = this.gravity
    val up: Boolean = gravity < 0
    val rayOffset = Vec3(0.0, if (up) 100.0 else -100.0, 0.0)
    val rayEnd: Vec3 = this.position().add(rayOffset)
    val raycast: BlockHitResult = level.clip(
        ClipContext(
            this.position().add(0.0, if (up) 0.01 else -2.01, 0.0),
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
private const val defaultLaunch = 0.42 * 1.8

fun Player.launch(multiplier: Double) {
    val vec3: Vec3 = this.deltaMovement
    val gravity = this.gravity
    val up = gravity > 0
    this.setDeltaMovement(vec3.x, defaultLaunch * multiplier * if (up) 1.0 else -1.0, vec3.z)
    if (this.isSprinting) {
        val rot: Float = this.yRot * (Math.PI / 180.0).toFloat()
        this.deltaMovement = this.deltaMovement.add((-Mth.sin(rot) * 0.2f).toDouble(), 0.0, (Mth.cos(rot) * 0.2f).toDouble())
    }

    this.hasImpulse = true
}

// Minecraft accessors

@Environment(EnvType.CLIENT)
inline fun input(): Input? = Minecraft.getInstance().player?.input

inline val Player.gdData get() = (this as PlayerDuck).`geometryDash$getGDData`()

inline var Entity.gravity: Double
    get() = (this as EntityDuck).`geometryDash$getGravity`()
    set(value) = (this as EntityDuck).`geometryDash$setGravity`(value)

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
