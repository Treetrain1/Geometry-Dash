package me.treetrain1.geometrydash.entity.render.model

import me.treetrain1.geometrydash.util.gdData
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.util.Mth

@Environment(EnvType.CLIENT)
open class SpiderPlayerModel<T : AbstractClientPlayer>(
    root: ModelPart
): GDPlayerModel<T>(root) {

    companion object {
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val root = meshDefinition.root
            root.addOrReplaceChild(
                "head", CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-2F, -2F, -2F, 4F, 4F, 4F),
                PartPose.ZERO
            )
            // TODO: make the legs actual legs
            root.addOrReplaceChild(
                "front_left_leg", CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-2F, -2F, -2F, 4F, 4F, 4F),
                PartPose.ZERO
            )
            root.addOrReplaceChild(
                "front_right_leg", CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-2F, -2F, -2F, 4F, 4F, 4F),
                PartPose.ZERO
            )
            root.addOrReplaceChild(
                "back_left_leg", CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-2F, -2F, -2F, 4F, 4F, 4F),
                PartPose.ZERO
            )
            root.addOrReplaceChild(
                "back_right_leg", CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-2F, -2F, -2F, 4F, 4F, 4F),
                PartPose.ZERO
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }

    private val head: ModelPart = root.getChild("head")

    override fun prepareMobModel(player: T, limbSwing: Float, limbSwingAmount: Float, partialTick: Float) {
        super.prepareMobModel(player, limbSwing, limbSwingAmount, partialTick)
        this.xRot =
            (player.gdData.modeData!!.getModelPitch(partialTick) % 360F) * Mth.DEG_TO_RAD
    }
}
