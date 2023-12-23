package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.block.JumpPadBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties

object RegisterBlocks {

    val JUMP_PAD: Block = register("jump_pad", JumpPadBlock(Properties.of()))

}
