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

        private const val Y_OFFSET: Float = 0.8F
    }

    override val scale: Float = 1F
    override val height: Float = 1F
    override val width: Float = 0.5F
    override val yOffset: Float = Y_OFFSET

    override fun getLayer(entity: Checkpoint): RenderType {
        return LAYER
    }

    override fun getTextureLocation(entity: Checkpoint): ResourceLocation = TEXTURE_ALT
}
