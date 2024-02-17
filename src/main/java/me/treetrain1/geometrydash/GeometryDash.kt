package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.command.GDCommand
import me.treetrain1.geometrydash.command.GDModeArgument
import me.treetrain1.geometrydash.command.GravityCommand
import me.treetrain1.geometrydash.datafix.GDDataFixer
import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.entity.Ring
import me.treetrain1.geometrydash.network.C2SFailPacket
import me.treetrain1.geometrydash.registry.*
import me.treetrain1.geometrydash.util.*
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

                entries.accept(RegisterItems.CUBE_PORTAL)
                entries.accept(RegisterItems.SHIP_PORTAL)
                entries.accept(RegisterItems.BALL_PORTAL)
                entries.accept(RegisterItems.UFO_PORTAL)
                entries.accept(RegisterItems.WAVE_PORTAL)
                entries.accept(RegisterItems.ROBOT_PORTAL)
                entries.accept(RegisterItems.SPIDER_PORTAL)
                entries.accept(RegisterItems.SWING_PORTAL)
                entries.accept(RegisterItems.CUBE_3D_PORTAL)
                entries.accept(RegisterItems.GRAVITY_FLIP_PORTAL)
                entries.accept(RegisterItems.MIRROR_PORTAL)
                entries.accept(RegisterItems.NORMAL_SCALE_PORTAL)
                entries.accept(RegisterItems.SMALL_SCALE_PORTAL)
                entries.accept(RegisterItems.LARGE_SCALE_PORTAL)
            }
            .build()
    ).key!!

    @JvmField
    val DIMENSION_TYPE: ResourceKey<DimensionType> = ResourceKey.create(Registries.DIMENSION_TYPE, id("geometry"))

    @JvmField
    val LEVEL_FAIL: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, id("level_fail"))

    @JvmField
    val VEC_SERIALIZER: EntityDataSerializer<Vec3> = EntityDataSerializer.simple(
        FriendlyByteBuf::writeVec3,
        FriendlyByteBuf::readVec3
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

            EntityDataSerializers.registerSerializer(VEC_SERIALIZER)
            EntityDataSerializers.registerSerializer(Checkpoint.CheckpointType.SERIALIZER)
            EntityDataSerializers.registerSerializer(Ring.RingType.SERIALIZER)
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
