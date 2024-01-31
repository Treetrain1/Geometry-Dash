package me.treetrain1.geometrydash.entity.render

import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.entity.Ring
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.resources.ResourceLocation

open class PortalRenderer(ctx: Context) : StaticEntityRenderer<Portal>(ctx) {

    companion object {
        private val CUBE_TEXTURE = id("textures/entity/cube_portal.png")
        private val CUBE_LAYER = RenderType.entityCutout(CUBE_TEXTURE)

        private val SHIP_TEXTURE = id("textures/entity/ship_portal.png")
        private val SHIP_LAYER = RenderType.entityCutout(SHIP_TEXTURE)

        private val BALL_TEXTURE = id("textures/entity/ball_portal.png")
        private val BALL_LAYER = RenderType.entityCutout(BALL_TEXTURE)

        private val UFO_TEXTURE = id("textures/entity/ufo_portal.png")
        private val UFO_LAYER = RenderType.entityCutout(UFO_TEXTURE)

        private val WAVE_TEXTURE = id("textures/entity/wave_portal.png")
        private val WAVE_LAYER = RenderType.entityCutout(WAVE_TEXTURE)

        private val ROBOT_TEXTURE = id("textures/entity/robot_portal.png")
        private val ROBOT_LAYER = RenderType.entityCutout(ROBOT_TEXTURE)

        private val SPIDER_TEXTURE = id("textures/entity/spider_portal.png")
        private val SPIDER_LAYER = RenderType.entityCutout(SPIDER_TEXTURE)

        private val SWING_TEXTURE = id("textures/entity/swing_portal.png")
        private val SWING_LAYER = RenderType.entityCutout(SWING_TEXTURE)
    }

    override fun scale(entity: Portal): Float = 1F
    override fun width(entity: Portal): Float = 1F
    override fun height(entity: Portal): Float = 2F
    override fun yOffset(entity: Portal): Float = 1F

    override fun getTextureLocation(entity: Portal): ResourceLocation {
        return when (entity.type) {
            Portal.PortalType.CUBE -> CUBE_TEXTURE
            Portal.PortalType.SHIP -> SHIP_TEXTURE
            Portal.PortalType.BALL -> BALL_TEXTURE
            Portal.PortalType.UFO -> UFO_TEXTURE
            Portal.PortalType.WAVE -> WAVE_TEXTURE
            Portal.PortalType.ROBOT -> ROBOT_TEXTURE
            Portal.PortalType.SPIDER -> SPIDER_TEXTURE
            Portal.PortalType.SWING -> SWING_TEXTURE
            else -> CUBE_TEXTURE
        }
    }

    override fun getLayer(entity: Portal): RenderType {
        return when (entity.type) {
            Portal.PortalType.CUBE -> CUBE_LAYER
            Portal.PortalType.SHIP -> SHIP_LAYER
            Portal.PortalType.BALL -> BALL_LAYER
            Portal.PortalType.UFO -> UFO_LAYER
            Portal.PortalType.WAVE -> WAVE_LAYER
            Portal.PortalType.ROBOT -> ROBOT_LAYER
            Portal.PortalType.SPIDER -> SPIDER_LAYER
            Portal.PortalType.SWING -> SWING_LAYER
            else -> CUBE_LAYER
        }
    }
}
