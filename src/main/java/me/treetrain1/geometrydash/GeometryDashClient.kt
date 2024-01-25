package me.treetrain1.geometrydash;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.entity.render.CheckpointRenderer;
import me.treetrain1.geometrydash.entity.render.PortalRenderer
import me.treetrain1.geometrydash.entity.render.RingRenderer
import me.treetrain1.geometrydash.entity.render.model.CubePlayerModel;
import me.treetrain1.geometrydash.network.GDModeSyncPacket;
import me.treetrain1.geometrydash.registry.RegisterBlocks
import me.treetrain1.geometrydash.registry.RegisterEntities;
import me.treetrain1.geometrydash.util.id
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType

@Environment(EnvType.CLIENT)
object GeometryDashClient : ClientModInitializer {

    @JvmField
    val CUBE_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "cube")

	@Override
    override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE) { packet, player, sender ->
            (player as PlayerDuck).`geometryDash$getGDData`().setGD(packet.mode, packet.scale)
        }

        val layerRegistry = BlockRenderLayerMap.INSTANCE
        layerRegistry.putBlock(RegisterBlocks.SPIKE, RenderType.cutout())

        EntityRendererRegistry.register(RegisterEntities.CHECKPOINT, ::CheckpointRenderer)
        EntityRendererRegistry.register(RegisterEntities.RING, ::RingRenderer)
        EntityRendererRegistry.register(RegisterEntities.PORTAL, ::PortalRenderer)

        EntityModelLayerRegistry.registerModelLayer(
            CUBE_PLAYER,
            CubePlayerModel.Companion::createBodyLayer
        );
    }
}
