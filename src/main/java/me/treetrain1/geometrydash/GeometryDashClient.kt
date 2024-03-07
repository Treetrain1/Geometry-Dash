package me.treetrain1.geometrydash;

import me.treetrain1.geometrydash.entity.render.CheckpointRenderer
import me.treetrain1.geometrydash.entity.render.OrbRenderer
import me.treetrain1.geometrydash.entity.render.PortalRenderer
import me.treetrain1.geometrydash.entity.render.model.*
import me.treetrain1.geometrydash.network.S2CSoundPacket
import me.treetrain1.geometrydash.registry.RegisterBlocks
import me.treetrain1.geometrydash.registry.RegisterEntities
import me.treetrain1.geometrydash.util.GDMusic
import me.treetrain1.geometrydash.util.id
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.RenderType

@Environment(EnvType.CLIENT)
object GeometryDashClient : ClientModInitializer {

    @JvmField
    val CUBE_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "cube")
    @JvmField
    val SHIP_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "ship")
    @JvmField
    val BALL_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "ball")
    @JvmField
    val UFO_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "ufo")
    @JvmField
    val WAVE_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "wave")
    @JvmField
    val ROBOT_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "robot")
    @JvmField
    val SPIDER_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "spider")
    @JvmField
    val SWING_PLAYER: ModelLayerLocation = ModelLayerLocation(id("player"), "swing")

	@Override
    override fun onInitializeClient() {
        val layerRegistry = BlockRenderLayerMap.INSTANCE
        layerRegistry.putBlock(RegisterBlocks.SPIKE, RenderType.translucent())

        EntityRendererRegistry.register(RegisterEntities.CHECKPOINT, ::CheckpointRenderer)
        EntityRendererRegistry.register(RegisterEntities.ORB, ::OrbRenderer)
        EntityRendererRegistry.register(RegisterEntities.PORTAL, ::PortalRenderer)

        EntityModelLayerRegistry.registerModelLayer(
            CUBE_PLAYER,
            CubePlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            SHIP_PLAYER,
            ShipPlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            BALL_PLAYER,
            BallPlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            UFO_PLAYER,
            UFOPlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            WAVE_PLAYER,
            WavePlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            ROBOT_PLAYER,
            RobotPlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            SPIDER_PLAYER,
            SpiderPlayerModel.Companion::createBodyLayer
        )
        EntityModelLayerRegistry.registerModelLayer(
            SWING_PLAYER,
            SwingPlayerModel.Companion::createBodyLayer
        )

        // TODO: remove, this is purely for testing
        ClientPlayNetworking.registerGlobalReceiver(S2CSoundPacket.TYPE) { packet, player, sender ->
            val clip = GDMusic.getSound("467339") // 467339 is "At the Speed of Light" by Dimrain47
            if (clip != null && clip.isValidOpenAlSource) {
                clip.play()
            }
        }
    }
}
