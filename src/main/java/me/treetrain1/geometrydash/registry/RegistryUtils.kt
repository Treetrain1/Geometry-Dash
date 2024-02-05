@file:Suppress("NOTHING_TO_INLINE")

package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Holder.Reference
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import virtuoel.pehkui.Pehkui
import virtuoel.pehkui.api.ScaleModifier
import virtuoel.pehkui.api.ScaleRegistries
import virtuoel.pehkui.api.ScaleType
import kotlin.jvm.optionals.getOrNull

internal inline fun <T : Any> Registry<T>.register(id: String, `object`: T): T
    = Registry.register(this, id(id), `object`)

internal inline fun <T : Any> Registry<T>.registerForHolder(id: String, `object`: T): Reference<T>
    = Registry.registerForHolder(this, id(id), `object`)

internal inline fun register(id: String, block: Block, registerBlockItem: Boolean = true): Block {
    val registered = Registry.register(BuiltInRegistries.BLOCK, id(id), block)
    if (registerBlockItem)
        Registry.register(BuiltInRegistries.ITEM, id(id), BlockItem(block, FabricItemSettings()))
    return registered
}

@Suppress("UNCHECKED_CAST")
internal inline fun <T : BlockEntity> register(id: String, blockEntity: FabricBlockEntityTypeBuilder.Factory<T>, vararg blocks: Block): BlockEntityType<T>
    = BuiltInRegistries.BLOCK_ENTITY_TYPE.register(id, FabricBlockEntityTypeBuilder.create(blockEntity, *blocks).build(null)) as BlockEntityType<T>

internal inline fun register(id: String, item: Item): Item
    = BuiltInRegistries.ITEM.register(id, item)

@Suppress("UNCHECKED_CAST")
internal inline fun <T : Entity?> register(id: String, entity: EntityType<T>): EntityType<T>
    = BuiltInRegistries.ENTITY_TYPE.register(id, entity) as EntityType<T>

internal inline fun register(id: String): Reference<SoundEvent>
    = BuiltInRegistries.SOUND_EVENT.registerForHolder(id, SoundEvent.createVariableRangeEvent(id(id)))

// MODDED

fun register(path: String, valueModifier: ScaleModifier?, vararg dependantModifiers: ScaleModifier): ScaleType {
    val builder = ScaleType.Builder.create()
        .affectsDimensions()

    if (valueModifier != null) {
        builder.addBaseValueModifier(valueModifier)
    }

    for (scaleModifier in dependantModifiers) {
        builder.addDependentModifier(scaleModifier)
    }

    return register(path, builder.build())
}

internal inline fun register(id: String, scaleType: ScaleType): ScaleType {
    ScaleRegistries.SCALE_TYPES[id(id)] = scaleType
    return scaleType
}

// CREATIVE TAB

internal inline fun register(id: String, tab: CreativeModeTab): CreativeModeTab
    = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(id), tab)

inline val CreativeModeTab.key: ResourceKey<CreativeModeTab>?
    get() = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(this).getOrNull()
