package me.treetrain1.geometrydash.datafix

import me.treetrain1.geometrydash.util.DATA_VERSION
import me.treetrain1.geometrydash.util.id
import net.fabricmc.loader.api.ModContainer
import net.minecraft.core.Direction
import net.minecraft.util.datafix.schemas.NamespacedSchema
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixerBuilder
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.QuiltDataFixes
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.SimpleFixes

object GDDataFixer {

    fun applyDataFixes(mod: ModContainer) {
        val builder = QuiltDataFixerBuilder(DATA_VERSION)
        builder.addSchema(0, QuiltDataFixes.BASE_SCHEMA)

        val schemaV1 = builder.addSchema(1, ::NamespacedSchema)
        SimpleFixes.addBlockStateRenameFix(builder, "Rename tip direction to facing", id("spike"), BlockStateProperties.VERTICAL_DIRECTION.name, Direction.UP.serializedName, BlockStateProperties.FACING.name, schemaV1)

        QuiltDataFixes.buildAndRegisterFixer(mod, builder)
    }
}
