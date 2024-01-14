package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@ModifyReturnValue(method = "getRespawnPosition", at = @At("RETURN"))
	private BlockPos spawnAtCheckpoint(BlockPos original) {
		GDData data = ((PlayerDuck) this).geometryDash$getGDData();
		BlockPos lastCheckpoint = data.getLastValidCheckpoint();
		if (lastCheckpoint == null) return original;
		return lastCheckpoint;
	}
}
