package me.treetrain1.geometrydash.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.frozenblock.lib.datagen.api.FrozenBiomeTagProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class GDBiomeTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FrozenBiomeTagProvider(output, registriesFuture) {

    override fun addTags(arg: HolderLookup.Provider) {
    }
}
