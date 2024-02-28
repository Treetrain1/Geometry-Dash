package me.treetrain1.geometrydash.mixin.portal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.GeometryDash;
import net.kyrptonaught.customportalapi.util.CustomTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomTeleporter.class)
public class CustomTeleporterMixin {

	@WrapOperation(method = "createDestinationPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"))
	private static BlockPos setGDPos(double x, double y, double z, Operation<BlockPos> original, ServerLevel destination) {
		if (destination.dimension() == GeometryDash.DIMENSION) {
			return original.call(0.0, 10.0, 0.0);
		}
		return original.call(x, y, z);
	}
}
