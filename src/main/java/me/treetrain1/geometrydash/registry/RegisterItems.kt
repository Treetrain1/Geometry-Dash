package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.item.EditTool
import net.minecraft.world.item.Item

object RegisterItems {

    @JvmField
    val EDIT_TOOL: Item = register(
        "edit_tool",
        EditTool()
    )
}
