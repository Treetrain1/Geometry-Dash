package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.command.GDCommand
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.network.GDModeSyncPacket
import me.treetrain1.geometrydash.registry.RegisterBlockEntities
import me.treetrain1.geometrydash.registry.RegisterBlocks
import me.treetrain1.geometrydash.registry.key
import me.treetrain1.geometrydash.registry.register
import me.treetrain1.geometrydash.util.log
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.system.measureNanoTime

object GeometryDash : ModInitializer {

    @JvmField
    val CREATIVE_TAB: ResourceKey<CreativeModeTab> = register(
        "creative_tab",
        FabricItemGroup.builder()
            .title(Component.literal("Geometry Dash"))
            .icon { ItemStack(Items.DIAMOND) }
            .displayItems { params, entries ->
                entries.accept(RegisterBlocks.LOW_JUMP_PAD)
                entries.accept(RegisterBlocks.JUMP_PAD)
                entries.accept(RegisterBlocks.HIGH_JUMP_PAD)
                entries.accept(RegisterBlocks.REVERSE_GRAVITY_JUMP_PAD)
                entries.accept(RegisterBlocks.TELEPORT_PAD)
            }
            .build()
    ).key!!

    override fun onInitialize() {
        val time = measureNanoTime {
            RegisterBlocks
            RegisterBlockEntities

            CommandRegistrationCallback.EVENT.register { dispatcher, ctx, selection ->
                GDCommand.register(dispatcher)
            }

            ServerPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE) { packet, player, sender ->
                (player as PlayerDuck).`geometryDash$setGDMode`(packet.mode)
            }
        }

        log("Geometry Dash took $time nanoseconds")
    }
}
