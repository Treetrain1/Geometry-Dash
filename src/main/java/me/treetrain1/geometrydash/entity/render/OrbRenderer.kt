package me.treetrain1.geometrydash.entity.render

import me.treetrain1.geometrydash.entity.Orb
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.resources.ResourceLocation

open class OrbRenderer(ctx: Context) : StaticEntityRenderer<Orb>(ctx) {

    companion object {
        private val TEXTURE = id("textures/entity/orb.png")
        private val LAYER = RenderType.entityCutout(TEXTURE)

        private val SMALL_BOUNCE_TEXTURE = id("textures/entity/small_bounce_orb.png")
        private val SMALL_BOUNCE_LAYER = RenderType.entityCutout(SMALL_BOUNCE_TEXTURE)

        private val BIG_BOUNCE_TEXTURE = id("textures/entity/big_bounce_orb.png")
        private val BIG_BOUNCE_LAYER = RenderType.entityCutout(BIG_BOUNCE_TEXTURE)

        private val SWING_TEXTURE = id("textures/entity/swing_orb.png")
        private val SWING_LAYER = RenderType.entityCutout(SWING_TEXTURE)

        private val REVERSE_GRAVITY_TEXTURE = id("textures/entity/reverse_gravity_orb.png")
        private val REVERSE_GRAVITY_LAYER = RenderType.entityCutout(REVERSE_GRAVITY_TEXTURE)

        private val FORCE_DOWN_TEXTURE = id("textures/entity/force_down_orb.png")
        private val FORCE_DOWN_LAYER = RenderType.entityCutout(FORCE_DOWN_TEXTURE)

        private val DASH_TEXTURE = id("textures/entity/dash_orb.png")
        private val DASH_LAYER = RenderType.entityCutout(DASH_TEXTURE)

        private val DASH_REVERSE_GRAVITY_TEXTURE = id("textures/entity/dash_reverse_gravity_orb.png")
        private val DASH_REVERSE_GRAVITY_LAYER = RenderType.entityCutout(DASH_REVERSE_GRAVITY_TEXTURE)

        private val TELEPORT_TEXTURE = id("textures/entity/teleport_orb.png")
        private val TELEPORT_LAYER = RenderType.entityCutout(TELEPORT_TEXTURE)
    }

    override fun scale(entity: Orb): Float = 1F
    override fun width(entity: Orb): Float = 1F
    override fun height(entity: Orb): Float = 1F
    override fun yOffset(entity: Orb): Float = 0.44F

    override fun getTextureLocation(entity: Orb): ResourceLocation {
        return when (entity.type) {
            Orb.OrbType.BOUNCE -> TEXTURE
            Orb.OrbType.SMALL_BOUNCE -> SMALL_BOUNCE_TEXTURE
            Orb.OrbType.BIG_BOUNCE -> BIG_BOUNCE_TEXTURE
            Orb.OrbType.SWING -> SWING_TEXTURE
            Orb.OrbType.REVERSE_GRAVITY -> REVERSE_GRAVITY_TEXTURE
            Orb.OrbType.FORCE_DOWN -> FORCE_DOWN_TEXTURE
            Orb.OrbType.DASH -> DASH_TEXTURE
            Orb.OrbType.DASH_REVERSE_GRAVITY -> DASH_REVERSE_GRAVITY_TEXTURE
            Orb.OrbType.TELEPORT -> TELEPORT_TEXTURE
            else -> TEXTURE
        }
    }

    override fun getLayer(entity: Orb): RenderType {
        return when (entity.type) {
            Orb.OrbType.BOUNCE -> LAYER
            Orb.OrbType.SMALL_BOUNCE -> SMALL_BOUNCE_LAYER
            Orb.OrbType.BIG_BOUNCE -> BIG_BOUNCE_LAYER
            Orb.OrbType.SWING -> SWING_LAYER
            Orb.OrbType.REVERSE_GRAVITY -> REVERSE_GRAVITY_LAYER
            Orb.OrbType.FORCE_DOWN -> FORCE_DOWN_LAYER
            Orb.OrbType.DASH -> DASH_LAYER
            Orb.OrbType.DASH_REVERSE_GRAVITY -> DASH_REVERSE_GRAVITY_LAYER
            Orb.OrbType.TELEPORT -> TELEPORT_LAYER
            else -> LAYER
        }
    }
}
