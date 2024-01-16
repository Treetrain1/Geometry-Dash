package me.treetrain1.geometrydash;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.entity.render.CheckpointRenderer;
import me.treetrain1.geometrydash.entity.render.model.CubePlayerModel;
import me.treetrain1.geometrydash.network.GDModeSyncPacket;
import me.treetrain1.geometrydash.registry.RegisterEntities;
import me.treetrain1.geometrydash.util.id
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
object GeometryDashClient : ClientModInitializer {

    @JvmField
    val CUBE_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "cube")

	@Override
    override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE) { packet, player, sender ->
            (player as PlayerDuck).`geometryDash$getGDData`().setGD(packet.mode, packet.scale)
        }

        EntityRendererRegistry.register(RegisterEntities.CHECKPOINT, ::CheckpointRenderer);

        EntityModelLayerRegistry.registerModelLayer(
            CUBE_PLAYER,
            CubePlayerModel.Companion::createBodyLayer
        );
    }
}