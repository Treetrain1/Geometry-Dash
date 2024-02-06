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
    override fun modID(): String {
        return MOD_ID
    }

    override fun biomeID(): String {
        return "geometry"
    }

    override fun temperature(): Float {
        return 0F
    }

    override fun downfall(): Float {
        return 0F
    }

    override fun hasPrecipitation(): Boolean {
        return false
    }

    override fun skyColor(): Int {
        return 0
    }

    override fun fogColor(): Int {
        return 0
    }

    override fun waterColor(): Int {
        return 0
    }

    override fun waterFogColor(): Int {
        return 0
    }

    override fun foliageColorOverride(): Int? {
        return null
    }

    override fun grassColorOverride(): Int? {
        return null
    }

    override fun ambientParticleSettings(): AmbientParticleSettings? {
        return null
    }

    override fun ambientLoopSound(): Holder<SoundEvent>? {
        return null
    }

    override fun ambientMoodSettings(): AmbientMoodSettings? {
        return null
    }

    override fun ambientAdditionsSound(): AmbientAdditionsSettings? {
        return null
    }

    override fun backgroundMusic(): Music? {
        return null
    }

    override fun addFeatures(features: BiomeGenerationSettings.Builder) {
    }

    override fun addSpawns(spawns: MobSpawnSettings.Builder) {
    }

    override fun injectToOverworld(parameters: Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>) {
    }
}
