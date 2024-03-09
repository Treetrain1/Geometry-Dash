package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.command.GDCommand
import me.treetrain1.geometrydash.command.GDModeArgument
import me.treetrain1.geometrydash.command.GravityCommand
import me.treetrain1.geometrydash.datafix.GDDataFixer
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.entity.Orb
import me.treetrain1.geometrydash.network.C2SExitPacket
import me.treetrain1.geometrydash.network.C2SFailPacket
import me.treetrain1.geometrydash.portal.RegisterPortal
import me.treetrain1.geometrydash.registry.*
import me.treetrain1.geometrydash.util.*
import me.treetrain1.geometrydash.worldgen.SchematicChunkGenerator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.frozenblock.lib.gravity.api.GravityAPI
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.phys.Vec3
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
                entries.accept(RegisterBlocks.RAINBOW_BLOCK)
                entries.accept(RegisterBlocks.RED_BLOCK)
                entries.accept(RegisterBlocks.ORANGE_BLOCK)
                entries.accept(RegisterBlocks.YELLOW_BLOCK)
                entries.accept(RegisterBlocks.LIME_BLOCK)
                entries.accept(RegisterBlocks.LIGHT_BLUE_BLOCK)
                entries.accept(RegisterBlocks.BLUE_BLOCK)
                entries.accept(RegisterBlocks.RAINBOW_BLOCK)

                entries.accept(RegisterItems.EDIT_TOOL)
            }
            .build()
    ).key!!

    @JvmField
    val SPECIAL_EFFECTS = id("geometry")

    @JvmField
    val DIMENSION_TYPE: ResourceKey<DimensionType> = ResourceKey.create(Registries.DIMENSION_TYPE, id("geometry"))

    @JvmField
    val DIMENSION: ResourceKey<Level> = ResourceKey.create(Registries.DIMENSION, id("geometry"))

    @JvmField
    val LEVEL_FAIL: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, id("level_fail"))

    @JvmField
    val DOUBLE_SERIALIZER: EntityDataSerializer<Double> = EntityDataSerializer.simple(
        FriendlyByteBuf::writeDouble,
        FriendlyByteBuf::readDouble
    )

    override fun onInitialize() {
        val time = measureNanoTime {
            GDRegistries
            GDDataFixer.applyDataFixes(FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow())

            RegisterBlocks
            RegisterBlockEntities
            RegisterEntities
            RegisterItems
            RegisterScaleTypes
            RegisterSounds

            BuiltInRegistries.CHUNK_GENERATOR.register("schematic", SchematicChunkGenerator.CODEC)

            RegisterPortal.init()

            ArgumentTypeInfos.register(
                BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
                string("gd_mode"),
                GDModeArgument::class.java,
                SingletonArgumentInfo.contextFree(GDModeArgument::gdMode)
            )

            CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
                GDCommand.register(dispatcher)
                GravityCommand.register(dispatcher)
            }

            ServerPlayNetworking.registerGlobalReceiver(C2SFailPacket.TYPE) { packet, player, _ ->
                player.hurt(player.damageSources().source(LEVEL_FAIL), Float.MAX_VALUE)
            }
            ServerPlayNetworking.registerGlobalReceiver(C2SExitPacket.TYPE) { packet, player, _ ->
                player.gdData.exitGD()
            }

            EntityDataSerializers.registerSerializer(DOUBLE_SERIALIZER)
            EntityDataSerializers.registerSerializer(Checkpoint.CheckpointType.SERIALIZER)
            EntityDataSerializers.registerSerializer(Orb.OrbType.SERIALIZER)
            EntityDataSerializers.registerSerializer(Portal.PortalType.SERIALIZER)

            GravityAPI.MODIFICATIONS.register { ctx ->
                val entity = ctx.entity ?: return@register
                val gravity = entity.gravity
                ctx.gravity = gravity
            }
        }

        log("Geometry Dash took $time nanoseconds")
    }
}
