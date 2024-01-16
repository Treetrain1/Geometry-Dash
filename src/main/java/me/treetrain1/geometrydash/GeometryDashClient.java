package me.treetrain1.geometrydash;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.entity.render.CheckpointRenderer;
import me.treetrain1.geometrydash.entity.render.model.CubePlayerModel;
import me.treetrain1.geometrydash.network.GDModeSyncPacket;
import me.treetrain1.geometrydash.registry.RegisterEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class GeometryDashClient implements ClientModInitializer {

    public static final ModelLayerLocation CUBE_PLAYER = new ModelLayerLocation(new ResourceLocation("geometry_dash", "player"), "cube");

	@Override
    public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE, ((packet, player, responseSender) -> {
			if (player instanceof PlayerDuck playerDuck) {
				playerDuck.geometryDash$getGDData().setGD(packet.mode, packet.scale);
			}
		}));

        EntityRendererRegistry.register(RegisterEntities.CHECKPOINT, CheckpointRenderer::new);

        EntityModelLayerRegistry.registerModelLayer( //This throws an error in Kotlin. Kotlin sucks.
            CUBE_PLAYER,
            CubePlayerModel::createBodyLayer
        );
    }
}
