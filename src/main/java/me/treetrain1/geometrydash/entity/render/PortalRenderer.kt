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
    }

    override fun scale(entity: Portal): Float = 1F
    override fun width(entity: Portal): Float = 1F
    override fun height(entity: Portal): Float = 2F
    override fun yOffset(entity: Portal): Float = 1F

    override fun getTextureLocation(entity: Portal): ResourceLocation {
        return when (entity.type) {
            Portal.PortalType.CUBE -> CUBE_TEXTURE
            else -> CUBE_TEXTURE
        }
    }

    override fun getLayer(entity: Portal): RenderType {
        return when (entity.type) {
            Portal.PortalType.CUBE -> CUBE_LAYER
            else -> CUBE_LAYER
        }
    }
}
