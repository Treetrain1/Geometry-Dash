package me.treetrain1.geometrydash.registry

import com.mojang.serialization.Codec
import me.treetrain1.geometrydash.data.mode.*
import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object GDRegistries {

    @JvmField
    val GD_MODE_DATA: Registry<Codec<out GDModeData>> = FabricRegistryBuilder.createDefaulted<Codec<out GDModeData>>(
        ResourceKey.createRegistryKey(id("gd_mode_data")),
        id("cube")
    ).attribute(RegistryAttribute.SYNCED).buildAndRegister()

    init {
        GDModeData.bootstrap(GD_MODE_DATA)
    }
}
