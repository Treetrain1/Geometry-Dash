package me.treetrain1.geometrydash.registry

object RegisterEntities {

    @JvmField
    val CHECKPOINT: EntityType<Checkpoint> = register(
        "checkpoint",
        FabricEntityTypeBuilder.<Checkpoint>create(MobCategory.MISC, ::Checkpoint)
            .fireImmune()
            .dimensions(EntityDimensions.scalable(0.7F, 1.6F))
            .trackRangeBlocks(16)
            .trackedUpdateRate(2)
            .build()
    )
}