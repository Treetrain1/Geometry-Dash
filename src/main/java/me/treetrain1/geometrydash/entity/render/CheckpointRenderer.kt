package me.treetrain1.geometrydash.entity.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.util.id
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.joml.Quaternionf

open class CheckpointRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<Checkpoint>(ctx) {

    companion object {
        private val TEXTURE = id("textures/entity/checkpoint.png")
        private val LAYER = RenderType.entityCutout(TEXTURE)

        private const val Y_OFFSET: Float = 0.8F
        private val QUAT_180: Quaternionf = Axis.YP.rotationDegrees(180F)
    }

    protected open fun getLayer(): RenderType = LAYER

    fun renderCheckpoint(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        tickDelta: Float,
        scale: Float,
        xOffset: Float,
        yOffset: Float,
        zOffset: Float,
        rot: Quaternionf
    ) {
        poseStack.pushPose()
        poseStack.scale(scale, scale, scale)
        poseStack.translate(xOffset, yOffset, zOffset)
        poseStack.mulPose(rot)
        poseStack.mulPose(QUAT_180)

        val entry = poseStack.last()
        val matrix4f: Matrix4f = entry.pose()
        val matrix3f = entry.normal()
        val vertexConsumer = buffer.getBuffer(this.getLayer())
        val overlay = OverlayTexture.NO_OVERLAY

        vertexConsumer
            .vertex(matrix4f, -0.25f, -0.5f, 0f)
            .color(255, 255, 255, 255)
            .uv(0f, 1f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, 0.25f, -0.5f, 0f)
            .color(255, 255, 255, 255)
            .uv(1f, 1f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, 0.25f, 0.5f, 0f)
            .color(255, 255, 255, 255)
            .uv(1f, 0f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, -0.25f, 0.5f, 0f)
            .color(255, 255, 255, 255)
            .uv(0f, 0f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()

        poseStack.popPose()
    }

    override fun render(
        entity: Checkpoint,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        val scale: Float = 1F
        renderCheckpoint(poseStack, buffer, packedLight, partialTick, scale, 0F, Y_OFFSET, 0F, this.entityRenderDispatcher.cameraOrientation())
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: Checkpoint): ResourceLocation = TEXTURE
}
