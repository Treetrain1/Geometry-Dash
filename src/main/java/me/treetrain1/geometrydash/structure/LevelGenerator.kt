package me.treetrain1.geometrydash.structure

import com.mojang.datafixers.util.Either
import me.treetrain1.geometrydash.util.id
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList
import java.util.function.Function


object LevelGenerator {

    @JvmField
    val GD_LEVEL: ResourceKey<StructureTemplatePool> = createKey("gd_level")

    /**
     * @param id                 The id for the [SinglePoolElement]'s [ResourceLocation]
     * @param processorListEntry The processor list for the [SinglePoolElement]
     * @return A [SinglePoolElement] of the parameters given.
     */
    fun ofProcessedSingle(id: String, processorListEntry: Holder<StructureProcessorList>): Function<StructureTemplatePool.Projection, SinglePoolElement> {
        return Function { projection -> SinglePoolElement(Either.left(id(id)), processorListEntry, projection) }
    }

    private fun createKey(id: String): ResourceKey<StructureTemplatePool>
        = ResourceKey.create(Registries.TEMPLATE_POOL, id(id))
}
