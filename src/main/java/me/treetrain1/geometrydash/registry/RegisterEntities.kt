package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.entity.Checkpoint
import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.entity.Orb
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object RegisterEntities {

    @JvmField
    val CHECKPOINT: EntityType<Checkpoint> = register(
        "checkpoint",
        FabricEntityTypeBuilder.create(MobCategory.MISC, ::Checkpoint)
            .fireImmune()
            .dimensions(EntityDimensions.scalable(0.7F, 1.6F))
            .trackRangeBlocks(32)
            .trackedUpdateRate(2)
            .build()
    )

    @JvmField
    val ORB: EntityType<Orb> = register(
        "orb",
        FabricEntityTypeBuilder.create(MobCategory.MISC, ::Orb)
            .fireImmune()
            .dimensions(EntityDimensions.scalable(0.9F, 0.9F))
            .trackRangeBlocks(32)
            .trackedUpdateRate(2)
            .build()
    )

    @JvmField
    val PORTAL: EntityType<Portal> = register(
        "portal",
        FabricEntityTypeBuilder.create(MobCategory.MISC, ::Portal)
            .fireImmune()
            // wide enough to fit an entire block (the player)
            .dimensions(EntityDimensions.scalable(1.05F, 1.92F))
            .trackRangeBlocks(32)
            .trackedUpdateRate(2)
            .build()
    )
}
