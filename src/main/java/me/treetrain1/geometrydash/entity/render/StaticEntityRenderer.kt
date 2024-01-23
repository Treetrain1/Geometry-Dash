package me.treetrain1.geometrydash.entity.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import me.treetrain1.geometrydash.entity.StaticEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import org.joml.Matrix4f
import org.joml.Quaternionf

abstract class StaticEntityRenderer<T : StaticEntity>(ctx: Context) : EntityRenderer<T>(ctx) {

    protected companion object {
        @JvmField
        val QUAT_180: Quaternionf = Axis.YP.rotationDegrees(180F)
    }

    protected abstract val scale: Float
    protected abstract val width: Float
    protected abstract val height: Float
    protected abstract val yOffset: Float
    protected open val overlay: Int = OverlayTexture.NO_OVERLAY

    protected abstract fun getLayer(entity: T): RenderType

    private inline val minX: Float get() = -width / 2
    private inline val maxX: Float get() = width / 2
    private inline val minY: Float get() = -height / 2
    private inline val maxY: Float get() = height / 2

    fun renderStaticEntity(
        entity: T,
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
        val vertexConsumer = buffer.getBuffer(this.getLayer(entity))
        val overlay = this.overlay
        val minX = this.minX
        val maxX = this.maxX
        val minY = this.minY
        val maxY = this.maxY

        vertexConsumer
            .vertex(matrix4f, minX, minY, 0f)
            .color(255, 255, 255, 255)
            .uv(0f, 1f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, maxX, minY, 0f)
            .color(255, 255, 255, 255)
            .uv(1f, 1f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, maxX, maxY, 0f)
            .color(255, 255, 255, 255)
            .uv(1f, 0f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()
        vertexConsumer
            .vertex(matrix4f, minX, maxY, 0f)
            .color(255, 255, 255, 255)
            .uv(0f, 0f)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(matrix3f, 0f, 1f, 0f)
            .endVertex()

        poseStack.popPose()
    }

    override fun render(
        entity: T,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        renderStaticEntity(entity, poseStack, buffer, packedLight, partialTick, scale, 0F, this.yOffset, 0F, this.entityRenderDispatcher.cameraOrientation())
        poseStack.popPose()
    }
}
