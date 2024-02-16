package me.treetrain1.geometrydash.util

import net.minecraft.world.phys.Vec3

enum class GravityDirection(val gravity: Vec3) {
    DOWN(Vec3(0.0, 1.0, 0.0)), // NORMAL
    UP(Vec3(0.0, -1.0, 0.0)), // FLIPPED
    NORTH(Vec3(-1.0, 0.0, 0.0)), // the rest of the values are 100% wrong
    SOUTH(Vec3(1.0, 0.0, 0.0)),
    EAST(Vec3(0.0, 0.0 -1.0)),
    WEST(Vec3(0.0, 0.0, 1.0))
}