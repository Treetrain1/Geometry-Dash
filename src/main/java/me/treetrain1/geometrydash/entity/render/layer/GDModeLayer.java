package me.treetrain1.geometrydash.entity.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.treetrain1.geometrydash.data.GDMode;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Environment(EnvType.CLIENT)
public class GDModeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	private final EntityModel<AbstractClientPlayer> model;
	private final GDMode requiredMode;

	public GDModeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModel<AbstractClientPlayer> newModel, GDMode requiredMode) {
		super(renderer);
		this.model = newModel;
		this.requiredMode = requiredMode;
	}

	@Override
	public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().mode == requiredMode) {
			this.model.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
			this.model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(player.getSkin().texture()));
			this.model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		}
	}

}
