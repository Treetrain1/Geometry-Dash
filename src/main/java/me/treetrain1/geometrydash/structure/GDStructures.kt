package me.treetrain1.geometrydash.structure

import me.treetrain1.geometrydash.util.id
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureSet

object GDStructures {

    @JvmField
    val GD_LEVELS: ResourceKey<StructureSet> = ResourceKey.create(Registries.STRUCTURE_SET, id("gd_levels"))

    @JvmField
    val GD_LEVEL: ResourceKey<Structure> = key("gd_level")

    private fun key(id: String): ResourceKey<Structure>
        = ResourceKey.create(Registries.STRUCTURE, id(id))
}
