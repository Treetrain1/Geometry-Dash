package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.worldgen.SchematicChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	@Inject(method = "method_38284", at = @At("HEAD"))
	private static void setSchematicLevel(ChunkStatus targetStatus, Executor executor, ServerLevel level, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ThreadedLevelLightEngine lightingProvider, Function chunkConverter, List chunks, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture> cir) {
		if (chunkGenerator instanceof SchematicChunkGenerator schem) {
			schem.init(level);
		}
	}
}
