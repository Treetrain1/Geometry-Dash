package me.treetrain1.geometrydash.portal

import me.treetrain1.geometrydash.registry.RegisterBlocks
import me.treetrain1.geometrydash.util.id
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

object RegisterPortal {

    internal fun init() {
        CustomPortalApiRegistry.registerPortalFrameTester(GeometryPortalHelper.ID, ::GeometryPortalHelper)

        CustomPortalBuilder.beginPortal()
            .frameBlock(Blocks.REINFORCED_DEEPSLATE)
            .lightWithItem(Items.ECHO_SHARD)
            .destDimID(id("geometry"))
            .returnDim(Level.OVERWORLD.location(), false)
            .tintColor(45, 65, 101)
            .forcedSize(20, 6)
            .customFrameTester(GeometryPortalHelper.ID)
            .registerPortal()
    }
}
