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
    }

    override val scale: Float = 1F
    override val width: Float = 1F
    override val height: Float = 1F
    override val yOffset: Float = 0F

    override fun getTextureLocation(entity: Ring): ResourceLocation {
        return TEXTURE
    }

    override fun getLayer(entity: Ring): RenderType {
        return LAYER
    }
}
