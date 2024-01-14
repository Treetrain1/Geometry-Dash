package me.treetrain1.geometrydash.registry

import me.treetrain1.geometrydash.entity.Checkpoint
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
            .trackRangeBlocks(16)
            .trackedUpdateRate(2)
            .build()
    )
}
