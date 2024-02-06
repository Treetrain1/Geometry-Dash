package me.treetrain1.geometrydash.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

class GDRegistryProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricDynamicRegistryProvider(output, registriesFuture) {
    override fun getName(): String
        = "Geometry Dash Dynamic Registries"

    override fun configure(registries: HolderLookup.Provider, entries: Entries) {
        entries.addAll(registries.lookupOrThrow(Registries.BIOME))
        entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE))
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE))
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET))
    }
}
