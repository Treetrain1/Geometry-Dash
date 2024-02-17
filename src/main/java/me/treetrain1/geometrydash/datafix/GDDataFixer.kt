package me.treetrain1.geometrydash.datafix

import me.treetrain1.geometrydash.util.DATA_VERSION
import net.fabricmc.loader.api.ModContainer
import net.minecraft.util.datafix.schemas.NamespacedSchema
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes

object GDDataFixer {

    fun applyDataFixes(mod: ModContainer) {
        val builder = QuiltDataFixerBuilder(DATA_VERSION)
        builder.addSchema(0, QuiltDataFixes.BASE_SCHEMA)

        val schemaV1 = builder.addSchema(1, ::NamespacedSchema)

        //QuiltDataFixes.buildAndRegisterFixer(mod, builder)
    }
}
