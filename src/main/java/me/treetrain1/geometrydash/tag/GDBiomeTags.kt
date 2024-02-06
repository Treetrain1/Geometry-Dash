package me.treetrain1.geometrydash.tag

import me.treetrain1.geometrydash.util.id
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome

object GDBiomeTags {

    @JvmField
    val GD_LEVEL_HAS_STRUCTURE: TagKey<Biome> = bind("has_structure/gd_level")

    private fun bind(path: String): TagKey<Biome> = TagKey.create(Registries.BIOME, id(path))
}
