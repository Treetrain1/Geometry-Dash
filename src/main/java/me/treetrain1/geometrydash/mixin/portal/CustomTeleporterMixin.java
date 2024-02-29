package me.treetrain1.geometrydash.mixin.portal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.treetrain1.geometrydash.GeometryDash;
import me.treetrain1.geometrydash.util.GDSharedConstantsKt;
import net.kyrptonaught.customportalapi.util.CustomTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomTeleporter.class)
public class CustomTeleporterMixin {

	@WrapOperation(method = "TPToDim", at = @At(value = "NEW", target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/ChunkPos;"))
	private static ChunkPos setGDPos(BlockPos pos, Operation<ChunkPos> original, @Local ServerLevel destination) {
		if (destination.dimension() == GeometryDash.DIMENSION) {
			return original.call(BlockPos.containing(GDSharedConstantsKt.DIMENSION_SPAWN));
		}
		return original.call(pos);
	}
}
