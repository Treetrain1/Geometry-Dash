package me.treetrain1.geometrydash.datagen

import me.treetrain1.geometrydash.GeometryDash
import me.treetrain1.geometrydash.biome.GeometryBiome
import me.treetrain1.geometrydash.structure.GDStructures
import me.treetrain1.geometrydash.tag.GDBiomeTags
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.Structures.structure
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure
import java.util.*

object GDDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()
        Registries.BIOME_SOURCE

        pack.addProvider(::GDRegistryProvider)
        pack.addProvider(::GDBiomeTagProvider)

        pack.addProvider(::GDBlockLootProvider)
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
        registryBuilder.add(Registries.BIOME) { ctx ->
            ctx.register(
                GeometryBiome.key,
                GeometryBiome.create(ctx)
            )
        }

        registryBuilder.add(Registries.DIMENSION_TYPE) { ctx ->
            ctx.register(
                GeometryDash.DIMENSION_TYPE, DimensionType(
                    OptionalLong.empty(),
                    true,
                    false,
                    false,
                    true,
                    1.0,
                    true,
                    false,
                    -64,
                    384,
                    384,
                    BlockTags.INFINIBURN_OVERWORLD,
                    BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                    0.0F,
                    DimensionType.MonsterSettings(true, false, ConstantInt.of(0), 0)
                )
            )
        }

        registryBuilder.add(Registries.STRUCTURE) { ctx ->
            ctx.register(
                GDStructures.STEREO_MADNESS,
                WoodlandMansionStructure(
                    structure(ctx.lookup(Registries.BIOME).getOrThrow(GDBiomeTags.GD_LEVEL_HAS_STRUCTURE), TerrainAdjustment.NONE)
                )
            )
        }

        registryBuilder.add(Registries.STRUCTURE_SET) { ctx ->
            val structures = ctx.lookup(Registries.STRUCTURE)
            ctx.register(
                GDStructures.GD_LEVELS,
                StructureSet(
                    listOf(
                        StructureSet.entry(structures.getOrThrow(GDStructures.STEREO_MADNESS))
                    ),
                    RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 0)
                )
            )
        }
    }
}
