@file:Suppress("NOTHING_TO_INLINE")

package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.util.id
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

internal inline fun <T : Any> Registry<T>.register(id: String, `object`: T): T
    = Registry.register(this, id(id), `object`)

internal inline fun register(id: String, block: Block): Block
    = BuiltInRegistries.BLOCK.register(id, block)

internal inline fun register(id: String, item: Item): Item
    = BuiltInRegistries.ITEM.register(id, item)

@Suppress("UNCHECKED_CAST")
internal inline fun <T : Entity?> register(id: String, entity: EntityType<T>): EntityType<T>
    = BuiltInRegistries.ENTITY_TYPE.register(id, entity) as EntityType<T>
