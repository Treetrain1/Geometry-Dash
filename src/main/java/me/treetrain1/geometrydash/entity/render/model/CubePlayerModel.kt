package me.treetrain1.geometrydash.entity.render.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import me.treetrain1.geometrydash.duck.PlayerDuck
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.util.function.Function

class CubePlayerModel<T : AbstractClientPlayer?>(private val root: ModelPart) : HierarchicalModel<T>(
    Function { location: ResourceLocation? -> RenderType.entityCutoutNoCull(location) }
) {

    companion object {
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            meshDefinition.root.addOrReplaceChild(
                "cube", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-4f, -4f, -4f, 8f, 8f, 8f), PartPose.ZERO
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }

    private val cube: ModelPart = root.getChild("cube")
    private var cubeRotation = 0f


    override fun prepareMobModel(player: T, limbSwing: Float, limbSwingAmount: Float, partialTick: Float) {
        root().allParts.forEach { obj: ModelPart -> obj.resetPose() }
        if (player is PlayerDuck) {
            this.cubeRotation =
                (player.`geometryDash$getGDData`().gdModeData!!.getModelPitch(partialTick) % 360f) * Mth.DEG_TO_RAD
        }
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.root.xRot = 0f
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, 1.25, 0.0)
        poseStack.mulPose(Axis.XP.rotation(this.cubeRotation))
        root().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha)
        poseStack.popPose()
    }

    override fun root(): ModelPart = this.root
}
