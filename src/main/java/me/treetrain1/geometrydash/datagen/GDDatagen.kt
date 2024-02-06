package me.treetrain1.geometrydash.datagen

import me.treetrain1.geometrydash.GeometryDash
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import java.util.*

object GDDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()
        Registries.BIOME_SOURCE

        pack.addProvider(::GDRegistryProvider)
        pack.addProvider(::GDBlockLootProvider)
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
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
    }
}
