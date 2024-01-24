package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.command.GDCommand
import me.treetrain1.geometrydash.command.GDModeArgument
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.entity.Ring
import me.treetrain1.geometrydash.network.C2SFailPacket
import me.treetrain1.geometrydash.network.GDModeSyncPacket
import me.treetrain1.geometrydash.registry.*
import me.treetrain1.geometrydash.util.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.frozenblock.lib.gravity.api.GravityAPI
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
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
                entries.accept(RegisterBlocks.SPIKE)
                entries.accept(RegisterBlocks.LOW_JUMP_PAD)
                entries.accept(RegisterBlocks.JUMP_PAD)
                entries.accept(RegisterBlocks.HIGH_JUMP_PAD)
                entries.accept(RegisterBlocks.REVERSE_GRAVITY_JUMP_PAD)
                entries.accept(RegisterBlocks.TELEPORT_PAD)
                entries.accept(RegisterItems.PORTAL_GUN)
            }
            .build()
    ).key!!

    @JvmField
    val LEVEL_FAIL: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, id("level_fail"))

    @JvmField
    val DOUBLE_SERIALIZER: EntityDataSerializer<Double> = EntityDataSerializer.simple(
        FriendlyByteBuf::writeDouble,
        FriendlyByteBuf::readDouble
    )

    override fun onInitialize() {
        val time = measureNanoTime {
            RegisterBlocks
            RegisterBlockEntities
            RegisterEntities
            RegisterItems

            ArgumentTypeInfos.register(
                BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
                string("gd_mode"),
                GDModeArgument::class.java,
                SingletonArgumentInfo.contextFree(GDModeArgument::gdMode)
            )

            CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
                GDCommand.register(dispatcher)
            }

            ServerPlayNetworking.registerGlobalReceiver(GDModeSyncPacket.PACKET_TYPE) { packet, player, _ ->
                (player as PlayerDuck).`geometryDash$getGDData`().setGD(packet.mode, packet.scale)
            }

            ServerPlayNetworking.registerGlobalReceiver(C2SFailPacket.TYPE) { packet, player, _ ->
                player.hurt(player.damageSources().source(LEVEL_FAIL), Float.MAX_VALUE)
            }

            EntityDataSerializers.registerSerializer(DOUBLE_SERIALIZER)
            EntityDataSerializers.registerSerializer(Checkpoint.CheckpointType.SERIALIZER)
            EntityDataSerializers.registerSerializer(Ring.RingType.SERIALIZER)

            GravityAPI.MODIFICATIONS.register { ctx ->
                val entity = ctx.entity ?: return@register
                val gravity = entity.gravity ?: return@register
                ctx.gravity = gravity
            }
        }

        log("Geometry Dash took $time nanoseconds")
    }
}
