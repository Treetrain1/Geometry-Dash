package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.block.CustomPortalBlock
import me.treetrain1.geometrydash.block.JumpPadBlock
import me.treetrain1.geometrydash.block.SpikeBlock
import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Blocks.always
import net.minecraft.world.level.block.Blocks.never
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction

object RegisterBlocks {

    @JvmField
    val SPIKE: Block = register(
        "spike",
        SpikeBlock(
            Properties.ofFullCopy(Blocks.POINTED_DRIPSTONE)
                .mapColor(MapColor.TERRACOTTA_BLACK)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .pushReaction(PushReaction.NORMAL)
        )
    )

    @JvmField
    val LOW_JUMP_PAD: Block = register(
        "low_jump_pad",
        jumpPad(JumpPadBlock.JumpPadType.LOW)
    )

    @JvmField
    val JUMP_PAD: Block = register(
        "jump_pad",
        jumpPad(JumpPadBlock.JumpPadType.NORMAL)
    )

    @JvmField
    val HIGH_JUMP_PAD: Block = register(
        "high_jump_pad",
        jumpPad(JumpPadBlock.JumpPadType.HIGH)
    )

    @JvmField
    val REVERSE_GRAVITY_JUMP_PAD: Block = register(
        "reverse_gravity_jump_pad",
        jumpPad(JumpPadBlock.JumpPadType.REVERSE_GRAVITY)
    )

    @JvmField
    val TELEPORT_PAD: Block = register(
        "teleport_pad",
        jumpPad(JumpPadBlock.JumpPadType.TELEPORT)
    )

    @JvmField
    val GEOMETRY_PORTAL: Block = register(
        "geometry_portal",
        CustomPortalBlock(
            Level.OVERWORLD, ResourceKey.create(Registries.DIMENSION, id("geometry")),
            Properties.ofFullCopy(Blocks.NETHER_PORTAL)
        )
    )
}

private inline fun jumpPad(type: JumpPadBlock.JumpPadType): JumpPadBlock
    = JumpPadBlock(
        type,
        FabricBlockSettings.create()
            .nonOpaque()
            .strength(0.1F)
            .emissiveLighting(::always)
            .luminance { 5 }
            .suffocates(::never)
            .blockVision(::never)
    )
