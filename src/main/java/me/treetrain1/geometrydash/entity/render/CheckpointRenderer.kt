package me.treetrain1.geometrydash.entity.render

import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

open class CheckpointRenderer(ctx: EntityRendererProvider.Context) : StaticEntityRenderer<Checkpoint>(ctx) {

    companion object {
        private val TEXTURE = id("textures/entity/checkpoint.png")
        private val TEXTURE_ALT = id("textures/entity/checkpoint_alt.png")
        private val LAYER = RenderType.entityCutout(TEXTURE)
    }

    override fun scale(entity: Checkpoint): Float = 1F
    override fun height(entity: Checkpoint): Float = 1F
    override fun width(entity: Checkpoint): Float = 0.5F
    override fun yOffset(entity: Checkpoint): Float = 0.8F

    override fun getLayer(entity: Checkpoint): RenderType {
        return LAYER
    }

    override fun getTextureLocation(entity: Checkpoint): ResourceLocation = TEXTURE_ALT
}
