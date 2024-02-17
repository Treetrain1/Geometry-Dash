package me.treetrain1.geometrydash.worldgen

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.biome.BiomeGenerationSettings.PlainBuilder
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull

// a Sponge V3 Schematic chunk generator
data class SchematicChunkGeneratorSettings(
    @JvmField
    val schematic: ResourceLocation,
    @JvmField
    val biome: Holder<Biome>
) {

    constructor(schematic: ResourceLocation, biome: Optional<Holder<Biome>>, default: Holder<Biome>) : this(schematic, biome.getOrNull() ?: default)

    companion object {
        @JvmField
        val CODEC: Codec<SchematicChunkGeneratorSettings> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("schematic").forGetter(SchematicChunkGeneratorSettings::schematic),
                Biome.CODEC.optionalFieldOf("biome").forGetter { settings: SchematicChunkGeneratorSettings -> Optional.of(settings.biome) },
                RegistryOps.retrieveElement(Biomes.THE_VOID)
            ).apply(instance, { schematic: ResourceLocation, biome: Optional<Holder<Biome>>, default: Holder<Biome> ->
                SchematicChunkGeneratorSettings(schematic, biome, default)
            })
        }.stable()
    }

    fun getGenSettings(biome: Holder<Biome>): BiomeGenerationSettings {
        return if (biome == this.biome) biome.value().generationSettings else PlainBuilder().build()
    }
}

open class SchematicChunkGenerator(
    val settings: SchematicChunkGeneratorSettings
) : ChunkGenerator(FixedBiomeSource(settings.biome), Util.memoize(settings::getGenSettings)) {

    companion object {
        @JvmField
        val CODEC: Codec<SchematicChunkGenerator> = RecordCodecBuilder.create { instance ->
            instance.group(
                SchematicChunkGeneratorSettings.CODEC.fieldOf("settings").forGetter(SchematicChunkGenerator::settings)
            ).apply(instance, ::SchematicChunkGenerator)
        }.stable()
    }

    private lateinit var schematic: Schematic

    fun init(level: ServerLevel) {
        if (::schematic.isInitialized) return
        val schematicId = settings.schematic
        schematic = Schematic.loadFromResource(level, schematicId)
    }

    override fun codec(): Codec<out ChunkGenerator> = CODEC

    override fun applyCarvers(
        level: WorldGenRegion,
        seed: Long,
        random: RandomState,
        biomeManager: BiomeManager,
        structureManager: StructureManager,
        chunk: ChunkAccess,
        step: GenerationStep.Carving
    ) {
        init(level.level)
    }

    override fun buildSurface(
        level: WorldGenRegion,
        structureManager: StructureManager,
        random: RandomState,
        chunk: ChunkAccess
    ) {
        init(level.level)
    }

    override fun spawnOriginalMobs(level: WorldGenRegion) {
        // TODO: Add the prebuilt world thing
        val serverLevel = level.level
        init(serverLevel)

        this.schematic.spawnMobs(level)
    }

    override fun getGenDepth(): Int = 384

    override fun fillFromNoise(
        executor: Executor,
        blender: Blender,
        random: RandomState,
        structureManager: StructureManager,
        chunk: ChunkAccess
    ): CompletableFuture<ChunkAccess> {
        this.schematic.placeChunk(chunk)

        return CompletableFuture.completedFuture(chunk)
    }

    override fun getSeaLevel(): Int = -63

    override fun getMinY(): Int = 0

    override fun getBaseHeight(
        x: Int,
        z: Int,
        type: Heightmap.Types,
        level: LevelHeightAccessor,
        random: RandomState
    ): Int = 5

    override fun getBaseColumn(x: Int, z: Int, height: LevelHeightAccessor, random: RandomState): NoiseColumn {
        return NoiseColumn(height.minBuildHeight, arrayOf(Blocks.STONE.defaultBlockState()))
    }

    override fun addDebugScreenInfo(info: MutableList<String>, random: RandomState, pos: BlockPos) {}
}
