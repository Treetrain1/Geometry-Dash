package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.item.PortalItem
import net.minecraft.world.item.Item

object RegisterItems {

    @JvmField
    val CUBE_3D_PORTAL: Item = register(
        "cube_3d_portal",
        PortalItem(Portal.PortalType.CUBE_3D)
    )

    @JvmField
    val GRAVITY_FLIP_PORTAL: Item = register(
        "gravity_flip_portal",
        PortalItem(Portal.PortalType.GRAVITY_FLIP)
    )

    @JvmField
    val MIRROR_PORTAL: Item = register(
        "mirror_portal",
        PortalItem(Portal.PortalType.MIRROR)
    )

    @JvmField
    val NORMAL_SCALE_PORTAL: Item = register(
        "normal_scale_portal",
        PortalItem(Portal.PortalType.SCALE_NORMAL)
    )

    @JvmField
    val SMALL_SCALE_PORTAL: Item = register(
        "small_scale_portal",
        PortalItem(Portal.PortalType.SCALE_SMALL)
    )

    @JvmField
    val LARGE_SCALE_PORTAL: Item = register(
        "large_scale_portal",
        PortalItem(Portal.PortalType.SCALE_LARGE)
    )
}
