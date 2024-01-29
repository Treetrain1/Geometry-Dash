package me.treetrain1.geometrydash.entity.render

import me.treetrain1.geometrydash.entity.Ring
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.resources.ResourceLocation

open class RingRenderer(ctx: Context) : StaticEntityRenderer<Ring>(ctx) {

    companion object {
        private val TEXTURE = id("textures/entity/ring.png")
        private val LAYER = RenderType.entityCutout(TEXTURE)

        private val SMALL_BOUNCE_TEXTURE = id("textures/entity/small_bounce_ring.png")
        private val SMALL_BOUNCE_LAYER = RenderType.entityCutout(SMALL_BOUNCE_TEXTURE)

        private val BIG_BOUNCE_TEXTURE = id("textures/entity/big_bounce_ring.png")
        private val BIG_BOUNCE_LAYER = RenderType.entityCutout(BIG_BOUNCE_TEXTURE)

        private val SWING_TEXTURE = id("textures/entity/swing_ring.png")
        private val SWING_LAYER = RenderType.entityCutout(SWING_TEXTURE)

        private val REVERSE_GRAVITY_TEXTURE = id("textures/entity/reverse_gravity_ring.png")
        private val REVERSE_GRAVITY_LAYER = RenderType.entityCutout(REVERSE_GRAVITY_TEXTURE)

        private val FORCE_DOWN_TEXTURE = id("textures/entity/force_down_ring.png")
        private val FORCE_DOWN_LAYER = RenderType.entityCutout(FORCE_DOWN_TEXTURE)

        private val TELEPORT_TEXTURE = id("textures/entity/teleport_ring.png")
        private val TELEPORT_LAYER = RenderType.entityCutout(TELEPORT_TEXTURE)
    }

    override fun scale(entity: Ring): Float = 1F
    override fun width(entity: Ring): Float = 1F
    override fun height(entity: Ring): Float = 1F
    override fun yOffset(entity: Ring): Float = 0.44F

    override fun getTextureLocation(entity: Ring): ResourceLocation {
        return when (entity.type) {
            Ring.RingType.BOUNCE -> TEXTURE
            Ring.RingType.SMALL_BOUNCE -> SMALL_BOUNCE_TEXTURE
            Ring.RingType.BIG_BOUNCE -> BIG_BOUNCE_TEXTURE
            Ring.RingType.SWING -> SWING_TEXTURE
            Ring.RingType.REVERSE_GRAVITY -> REVERSE_GRAVITY_TEXTURE
            Ring.RingType.FORCE_DOWN -> FORCE_DOWN_TEXTURE
            Ring.RingType.TELEPORT -> TELEPORT_TEXTURE
            else -> TEXTURE
        }
    }

    override fun getLayer(entity: Ring): RenderType {
        return when (entity.type) {
            Ring.RingType.BOUNCE -> LAYER
            Ring.RingType.SMALL_BOUNCE -> SMALL_BOUNCE_LAYER
            Ring.RingType.BIG_BOUNCE -> BIG_BOUNCE_LAYER
            Ring.RingType.SWING -> SWING_LAYER
            Ring.RingType.REVERSE_GRAVITY -> REVERSE_GRAVITY_LAYER
            Ring.RingType.FORCE_DOWN -> FORCE_DOWN_LAYER
            Ring.RingType.TELEPORT -> TELEPORT_LAYER
            else -> LAYER
        }
    }
}
