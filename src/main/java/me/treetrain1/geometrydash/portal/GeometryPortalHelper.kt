package me.treetrain1.geometrydash.portal

import me.treetrain1.geometrydash.GeometryDash
import me.treetrain1.geometrydash.duck.PortalDuck
import me.treetrain1.geometrydash.util.DIMENSION_SPAWN
import me.treetrain1.geometrydash.util.id
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester
import net.kyrptonaught.customportalapi.portal.frame.VanillaPortalAreaHelper
import net.minecraft.BlockUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.PortalInfo
import net.minecraft.world.phys.Vec3

open class GeometryPortalHelper : VanillaPortalAreaHelper() {

    companion object {
        @JvmField
        val ID: ResourceLocation = id("geometry")
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun isGD(level: LevelAccessor?): Boolean
        = level is Level && level.dimension() == GeometryDash.DIMENSION

    private inline val isGD: Boolean get() = isGD(this.`access$world`)

    override fun init(
        world: LevelAccessor?,
        blockPos: BlockPos?,
        axis: Direction.Axis?,
        vararg foundations: Block?
    ): PortalFrameTester {
        super.init(world, blockPos, axis, *foundations)

        if (isGD(world)) {
            this.lowerCorner = BlockPos(24, 10, 955)
            this.width = 3
            this.height = 3
            this.foundPortalBlocks = 9
        }
        return this
    }

    override fun isValidFrame(): Boolean {
        if (isGD) {
            return true
        }
        return super.isValidFrame()
    }

    // should go to ancient city but this is a failsafe to prevent being stuck in the gd dimension
    override fun createPortal(world: Level, pos: BlockPos, frameBlock: BlockState, axis: Direction.Axis) {
        if (isGD(world)) {
            this.lowerCorner = BlockPos(16, 20, 959)
            this.width = 3
            this.height = 3
            this.axis = axis
            this.foundPortalBlocks = 9
        } else super.createPortal(world, pos, frameBlock, axis)
    }

    override fun getTPTargetInPortal(
        portalRect: BlockUtil.FoundRectangle,
        portalAxis: Direction.Axis,
        prevOffset: Vec3,
        entity: Entity
    ): PortalInfo {
        if (isGD) {
            return PortalInfo(DIMENSION_SPAWN, entity.deltaMovement, entity.yRot, entity.xRot)
        }
        val portalPos = (entity as PortalDuck).`geometryDash$getGDPortalPos`()
        if (portalPos != null) {
            return PortalInfo(portalPos, entity.deltaMovement, entity.yRot, entity.xRot)
        }
        return super.getTPTargetInPortal(portalRect, portalAxis, prevOffset, entity)
    }

    override fun fillAirAroundPortal(world: Level, pos: BlockPos) {
        if (!isGD(world)) super.fillAirAroundPortal(world, pos)
    }

    override fun placeLandingPad(world: Level, pos: BlockPos, frameBlock: BlockState) {
        if (!isGD(world)) super.placeLandingPad(world, pos, frameBlock)
    }

    @PublishedApi
    internal var `access$world`: LevelAccessor?
        get() = world
        set(value) {
            world = value
        }
}
