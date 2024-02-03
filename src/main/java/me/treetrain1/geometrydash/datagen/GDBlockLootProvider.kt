package me.treetrain1.geometrydash.datagen

import me.treetrain1.geometrydash.registry.RegisterBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.Direction
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.MultifaceBlock
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue


class GDBlockLootProvider(dataOutput: FabricDataOutput) : FabricBlockLootTableProvider(dataOutput) {

    override fun generate() {
        this.dropSelf(RegisterBlocks.SPIKE)

        this.add(RegisterBlocks.LOW_JUMP_PAD, ::createMultifaceBlockDrops)
        this.add(RegisterBlocks.JUMP_PAD, ::createMultifaceBlockDrops)
        this.add(RegisterBlocks.HIGH_JUMP_PAD, ::createMultifaceBlockDrops)
        this.add(RegisterBlocks.REVERSE_GRAVITY_JUMP_PAD, ::createMultifaceBlockDrops)
        this.add(RegisterBlocks.TELEPORT_PAD, ::createMultifaceBlockDrops)
    }
}

fun BlockLootSubProvider.createMultifaceBlockDrops(block: Block): LootTable.Builder? {
    return LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .add(
                    this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(block)
                            .apply(Direction.entries.toTypedArray()) { direction -> SetItemCountFunction.setCount(ConstantValue.exactly(1.0f), true)
                                .`when`(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(
                                            StatePropertiesPredicate.Builder.properties().hasProperty(MultifaceBlock.getFaceProperty(direction), true)
                                        )
                                )
                            }
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0f), true))
                    ) as LootPoolEntryContainer.Builder<*>
                )
        )
}
