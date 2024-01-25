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

@Environment(EnvType.CLIENT)
open class GDPlayerModel<T : AbstractClientPlayer?>(protected val root: ModelPart) : HierarchicalModel<T>(
    Function { location: ResourceLocation? -> RenderType.entityCutoutNoCull(location) }
) {

    protected open var xOffset: Double = 0.0
    protected open var yOffset: Double = 1.25
    protected open var zOffset: Double = 0.0

    protected open var xRot: Float = 0F
    protected open var yRot: Float = 0F
    protected open var zRot: Float = 0F

    override fun prepareMobModel(player: T, limbSwing: Float, limbSwingAmount: Float, partialTick: Float) {
        root().allParts.forEach { obj: ModelPart -> obj.resetPose() }
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.root.xRot = 0F
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
        poseStack.translate(this.xOffset, this.yOffset, this.zOffset)
        poseStack.mulPose(Axis.XP.rotation(this.xRot))
        poseStack.mulPose(Axis.YP.rotation(this.yRot))
        poseStack.mulPose(Axis.ZP.rotation(this.zRot))
        root().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha)
        poseStack.popPose()
    }

    override fun root(): ModelPart = this.root
}
