package me.treetrain1.geometrydash.biome

import com.mojang.datafixers.util.Pair
import me.treetrain1.geometrydash.util.MOD_ID
import net.frozenblock.lib.worldgen.biome.api.FrozenBiome
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.biome.*
import java.util.function.Consumer

object GeometryBiome : FrozenBiome() {
    override fun modID(): String = MOD_ID

    override fun biomeID(): String = "geometry"

    override fun temperature(): Float = 0F

    override fun downfall(): Float = 0F

    override fun hasPrecipitation(): Boolean = false

    override fun skyColor(): Int = 0

    override fun fogColor(): Int = 0

    override fun waterColor(): Int = 0

    override fun waterFogColor(): Int = 0

    override fun foliageColorOverride(): Int? = null

    override fun grassColorOverride(): Int? = null

    override fun ambientParticleSettings(): AmbientParticleSettings? = null

    override fun ambientLoopSound(): Holder<SoundEvent>? = null

    override fun ambientMoodSettings(): AmbientMoodSettings? = null

    override fun ambientAdditionsSound(): AmbientAdditionsSettings? = null

    override fun backgroundMusic(): Music? = null

    override fun addFeatures(features: BiomeGenerationSettings.Builder) {
    }

    override fun addSpawns(spawns: MobSpawnSettings.Builder) {
    }

    override fun injectToOverworld(parameters: Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>) {
    }
}
