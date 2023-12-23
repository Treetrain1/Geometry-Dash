package me.treetrain1.geometrydash

import me.treetrain1.geometrydash.registry.RegisterBlockEntities
import me.treetrain1.geometrydash.registry.RegisterBlocks
import me.treetrain1.geometrydash.registry.key
import me.treetrain1.geometrydash.registry.register
import me.treetrain1.geometrydash.util.log
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.system.measureNanoTime

object GeometryDash : ModInitializer {

    @JvmField
    val CREATIVE_TAB: ResourceKey<CreativeModeTab> = register(
        "creative_tab",
        FabricItemGroup.builder()
            .title(Component.literal("Geometry Dash"))
            .icon { ItemStack(Items.DIAMOND) }
            .displayItems { params, entries ->}
            .build()
    ).key!!

    override fun onInitialize() {
        val time = measureNanoTime {
            RegisterBlocks
            RegisterBlockEntities
        }

        log("Geometry Dash took $time nanoseconds")
    }
}
