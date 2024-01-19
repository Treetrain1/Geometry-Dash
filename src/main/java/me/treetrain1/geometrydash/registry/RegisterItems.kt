package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.item.PortalItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties

object RegisterItems {

    @JvmField
    val PORTAL_GUN: Item = register(
        "portal_gun",
        PortalItem(
            Properties()
                .stacksTo(1)
        )
    )
}
