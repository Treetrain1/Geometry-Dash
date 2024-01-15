package me.treetrain1.geometrydash.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.duck.PlayerModelDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> implements PlayerModelDuck {

	@Shadow
	@Final
	private List<ModelPart> parts;

	@Unique
	private boolean isGDMode;

	@Unique
	private ModelPart gdHead;

	public PlayerModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(
		method = "<init>",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/PlayerModel;jacket:Lnet/minecraft/client/model/geom/ModelPart;")
	)
	public void gd$init(ModelPart root, boolean slim, CallbackInfo ci) {
		this.gdHead = root.getChild("gd_head");
	}

	@Inject(
		method = "createMesh",
		at = @At(value = "RETURN", shift = At.Shift.BEFORE),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void gd$createMesh(CubeDeformation cubeDeformation, boolean slim, CallbackInfoReturnable<MeshDefinition> cir, MeshDefinition meshDefinition) {
		meshDefinition.getRoot().addOrReplaceChild("gd_head", CubeListBuilder.create().texOffs(0, 0)
			.addBox(-4F, -4F, -4F, 8F, 8F, 8F, cubeDeformation), PartPose.offset(0F, 0F, 0F));

	}

	@Unique
	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.parts.forEach(ModelPart::resetPose);
		if (entity instanceof Player player && player instanceof PlayerDuck playerDuck) {
			if (!playerDuck.geometryDash$getGDData().getPlayingGD()) {
				this.isGDMode = false;
				super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			} else {
				this.isGDMode = true;
				this.gdHead.y -= 9F;
			}
		}
	}

	@Inject(
		method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift = At.Shift.BEFORE),
		cancellable = true
	)
	public void gd$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (this.isGDMode && entity instanceof PlayerDuck playerDuck) {
			ci.cancel();
			float tickDelta = ageInTicks - entity.tickCount;
			this.gdHead.xRot = playerDuck.geometryDash$getGDData().getJumpRotation(tickDelta) * Mth.DEG_TO_RAD;
			this.gdHead.z -= 2F;
			this.swimAmount = 0F;
		}
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.isGDMode) {
			this.gdHead.visible = true;
			poseStack.pushPose();
			poseStack.translate(0D, 1.3D, 0D);
			super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		} else {
			super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	@NotNull
	@Override
	public ModelPart geometryDash$getGDHead() {
		return this.gdHead;
	}
}
