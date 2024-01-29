package me.treetrain1.geometrydash.entity.render

import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.entity.Ring
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.resources.ResourceLocation

open class PortalRenderer(ctx: Context) : StaticEntityRenderer<Portal>(ctx) {

    companion object {
        private val TEXTURE = id("textures/entity/ring.png")
        private val LAYER = RenderType.entityCutout(TEXTURE)
    }

    override fun scale(entity: Portal): Float = 1F
    override fun width(entity: Portal): Float = 1F
    override fun height(entity: Portal): Float = 1F
    override fun yOffset(entity: Portal): Float = 0.44F

    override fun getTextureLocation(entity: Portal): ResourceLocation {
        return when (entity.type) {
            else -> TEXTURE
        }
    }

    override fun getLayer(entity: Portal): RenderType {
        return when (entity.type) {
            else -> LAYER
        }
    }
}
