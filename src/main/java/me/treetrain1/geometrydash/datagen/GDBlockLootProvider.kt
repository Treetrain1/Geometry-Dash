package me.treetrain1.geometrydash.datagen

import me.treetrain1.geometrydash.registry.RegisterBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider

class GDBlockLootProvider(dataOutput: FabricDataOutput) : FabricBlockLootTableProvider(dataOutput) {

    override fun generate() {
        this.dropSelf(RegisterBlocks.LOW_JUMP_PAD)
    }
}
