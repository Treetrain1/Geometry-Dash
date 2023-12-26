package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.block.entity.JumpPadBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object RegisterBlockEntities {

    @JvmField
    val JUMP_PAD: BlockEntityType<JumpPadBlockEntity> = register(
        "jump_pad",
        ::JumpPadBlockEntity,
        RegisterBlocks.JUMP_PAD, RegisterBlocks.REVERSE_GRAVITY_JUMP_PAD, RegisterBlocks.TELEPORT_PAD
    )
}
