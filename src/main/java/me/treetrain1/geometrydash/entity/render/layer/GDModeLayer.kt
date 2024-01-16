package me.treetrain1.geometrydash.entity.render.layer

import com.mojang.blaze3d.vertex.PoseStack
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.duck.PlayerDuck
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture

@Environment(EnvType.CLIENT)
open class GDModeLayer(
    renderer: RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>,
    private val model: EntityModel<AbstractClientPlayer>,
    private val requiredMode: GDMode
) : RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(renderer) {
    override fun render(
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        player: AbstractClientPlayer,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (player is PlayerDuck && player.`geometryDash$getGDData`().mode == requiredMode) {
            model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks)
            model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
            val vertexConsumer = buffer.getBuffer(RenderType.entityCutout(player.skin.texture()))
            model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)
        }
    }
}
