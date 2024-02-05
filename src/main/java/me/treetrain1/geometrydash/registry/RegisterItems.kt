package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.item.PortalItem
import net.minecraft.world.item.Item

object RegisterItems {

    @JvmField
    val CUBE_PORTAL: Item = register(
        "cube_portal",
        PortalItem()
    )

    @JvmField
    val SHIP_PORTAL: Item = register(
        "ship_portal",
        PortalItem(Portal.PortalType.SHIP)
    )

    @JvmField
    val BALL_PORTAL: Item = register(
        "ball_portal",
        PortalItem(Portal.PortalType.BALL)
    )

    @JvmField
    val UFO_PORTAL: Item = register(
        "ufo_portal",
        PortalItem(Portal.PortalType.UFO)
    )

    @JvmField
    val WAVE_PORTAL: Item = register(
        "wave_portal",
        PortalItem(Portal.PortalType.WAVE)
    )

    @JvmField
    val ROBOT_PORTAL: Item = register(
        "robot_portal",
        PortalItem(Portal.PortalType.ROBOT)
    )

    @JvmField
    val SPIDER_PORTAL: Item = register(
        "spider_portal",
        PortalItem(Portal.PortalType.SPIDER)
    )

    @JvmField
    val SWING_PORTAL: Item = register(
        "swing_portal",
        PortalItem(Portal.PortalType.SWING)
    )

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
