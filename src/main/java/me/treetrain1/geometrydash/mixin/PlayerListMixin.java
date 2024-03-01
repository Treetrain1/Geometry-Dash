package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import java.util.Optional;
import me.treetrain1.geometrydash.data.CheckpointSnapshot;
import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import me.treetrain1.geometrydash.entity.Checkpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRespawnPosition()Lnet/minecraft/core/BlockPos;"))
	private BlockPos useGD(ServerPlayer instance, Operation<BlockPos> original, @Share("isGD") LocalBooleanRef isGD) {
		GDData data = ((PlayerDuck) instance).geometryDash$getGDData();
		CheckpointSnapshot lastCheckpoint = data.getLastValidCheckpoint();
		if (lastCheckpoint != null) {
			Entity entity = data.getLevel().getEntity(lastCheckpoint.entityId);
			if (entity == null) {
				isGD.set(false);
				return original.call(instance);
			}
			isGD.set(true);
			return entity.blockPosition();
		}
		isGD.set(false);
		return original.call(instance);
	}

	@WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
	private Optional<Vec3> respawn(ServerLevel serverLevel, BlockPos spawnBlockPos, float playerOrientation, boolean isRespawnForced, boolean respawnAfterWinningTheGame, Operation<Optional<Vec3>> original, @Share("isGD") LocalBooleanRef isGD) {
		if (isGD.get()) {
			return Optional.of(Vec3.atBottomCenterOf(spawnBlockPos));
		}
		return original.call(serverLevel, spawnBlockPos, playerOrientation, isRespawnForced, respawnAfterWinningTheGame);
	}

	@Inject(method = "respawn", at = @At("RETURN"))
	private void restoreCheckpoint(ServerPlayer player, boolean b, CallbackInfoReturnable<ServerPlayer> cir) {
		GDData data = ((PlayerDuck) player).geometryDash$getGDData();
		CheckpointSnapshot lastCheckpoint = data.getLastValidCheckpoint();
		if (lastCheckpoint != null) {
			Entity entity = data.getLevel().getEntity(lastCheckpoint.entityId);
			if (entity instanceof Checkpoint checkpoint) {
				CheckpointSnapshot.Companion.restoreCheckpoint(player, data, checkpoint, lastCheckpoint);
			}
		}
	}
}
