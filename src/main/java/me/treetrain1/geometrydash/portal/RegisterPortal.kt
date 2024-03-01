package me.treetrain1.geometrydash.portal

import me.treetrain1.geometrydash.duck.PortalDuck
import me.treetrain1.geometrydash.util.id
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.kyrptonaught.customportalapi.util.SHOULDTP
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
            .registerBeforeTPEvent { entity ->
                if (entity.level().dimension() == Level.OVERWORLD) {
                    (entity as PortalDuck).`geometryDash$setGDPortalPos`()
                }
                SHOULDTP.CONTINUE_TP
            }
            .registerPortal()
    }
}
