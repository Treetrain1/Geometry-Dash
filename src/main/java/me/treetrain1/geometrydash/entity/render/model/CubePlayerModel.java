package me.treetrain1.geometrydash.entity.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CubePlayerModel<T extends AbstractClientPlayer> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart cube;
	private float cubeRotation;

	public CubePlayerModel(@NotNull ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
		this.cube = root.getChild("cube");
	}


	@NotNull
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		meshdefinition.getRoot().addOrReplaceChild("cube", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-4F, -4F, -4F, 8F, 8F, 8F), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void prepareMobModel(@NotNull T player, float limbSwing, float limbSwingAmount, float partialTick) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (player instanceof PlayerDuck playerDuck) {
			this.cubeRotation = (playerDuck.geometryDash$getGDData().gdModeData.getModelPitch(partialTick) % 360F) * Mth.DEG_TO_RAD;
		}
	}

	@Override
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root.xRot = 0F;
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		poseStack.translate(0D, 1.25D, 0D);
		poseStack.mulPose(Axis.XP.rotation(this.cubeRotation));
		this.root().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
		poseStack.popPose();
	}

	@Override
	@NotNull
	public ModelPart root() {
		return this.root;
	}
}
