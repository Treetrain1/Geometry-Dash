package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.render.CheckpointRenderer
import me.treetrain1.geometrydash.network.GDModeSyncPacket
import me.treetrain1.geometrydash.registry.RegisterEntities
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

@Environment(EnvType.CLIENT)
object GeometryDashClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE) { packet, player, _ ->
            val data = (player as PlayerDuck).`geometryDash$getGDData`()
            data.setGD(packet.mode, packet.scale)
        }

        EntityRendererRegistry.register(RegisterEntities.CHECKPOINT, ::CheckpointRenderer)
    }
}
