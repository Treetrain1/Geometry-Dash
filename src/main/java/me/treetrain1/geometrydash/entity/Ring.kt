package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.util.gdData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

// Usually called Orbs but in the files I believe it's called Rings
open class Ring(
    type: EntityType<out Ring>,
    level: Level
) : StaticEntity(type, level) {

    companion object {
        protected fun Entity.flipGravity() {
            // TODO: implement or just use the thing from jump pads
        }

        protected fun Entity.bounce(strength: Double) {
            // TODO: implement
        }

        protected fun Entity.teleport() {
            // TODO: implement
        }
    }

    // TODO: Convert to entity data accessor
    @JvmField
    var type: RingType = RingType.BOUNCE

    open fun onJump(player: Player, data: GDData) {
        val type = this.type
        if (type.shouldBounce) {
            player.bounce(type.bounceStrength)
        }
        if (type.shouldFlipGravity) {
            player.flipGravity()
        }
        if (type.shouldTeleport) {
            player.teleport()
        }
    }

    // TODO: double check the names
    enum class RingType(
        val shouldBounce: Boolean = true,
        val bounceStrength: Double = 1.0,
        val shouldFlipGravity: Boolean = false,
        val shouldTeleport: Boolean = false,
    ) {
        SMALL_BOUNCE(bounceStrength = 0.5), // purple/pink
        BOUNCE, // yellow
        BIG_BOUNCE(bounceStrength = 1.5), // red
        SWING(shouldBounce = false, shouldFlipGravity = true), // green
        REVERSE_GRAVITY(shouldFlipGravity = true), // blue
        TELEPORT(shouldBounce = false, shouldFlipGravity = true, shouldTeleport = true), // pink with arrows
    }
}