package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.block.JumpPadBlock
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.always
import net.minecraft.world.level.block.Blocks.never
import net.minecraft.world.level.block.state.BlockBehaviour.Properties

object RegisterBlocks {

    val JUMP_PAD: Block = register(
        "jump_pad",
        jumpPad()
    )

    val REVERSE_GRAVITY_JUMP_PAD: Block = register(
        "reverse_gravity_jump_pad",
        jumpPad(JumpPadBlock.JumpPadType.REVERSE_GRAVITY)
    )

    val TELEPORT_PAD: Block = register(
        "teleport_pad",
        jumpPad(JumpPadBlock.JumpPadType.TELEPORT)
    )
}

private fun jumpPad(type: JumpPadBlock.JumpPadType = JumpPadBlock.JumpPadType.NORMAL): JumpPadBlock
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
