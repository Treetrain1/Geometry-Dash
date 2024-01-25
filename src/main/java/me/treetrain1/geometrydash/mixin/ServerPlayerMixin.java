package me.treetrain1.geometrydash.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.treetrain1.geometrydash.data.CheckpointSnapshot;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@ModifyReturnValue(method = "getRespawnPosition", at = @At("RETURN"))
	private BlockPos spawnAtCheckpoint(BlockPos original) {
		var data = ((PlayerDuck) this).geometryDash$getGDData();
		CheckpointSnapshot lastCheckpoint = data.getLastValidCheckpoint();
		if (lastCheckpoint != null) {
			Entity entity = data.getLevel().getEntity(lastCheckpoint.getEntityId());
			if (entity != null) {
				return entity.blockPosition();
			}
		}
		return original;
	}

	@ModifyReturnValue(method = "getRespawnAngle", at = @At("RETURN"))
	private float checkpointAngle(float original) {
		var data = ((PlayerDuck) this).geometryDash$getGDData();
		CheckpointSnapshot lastCheckpoint = data.getLastValidCheckpoint();
		if (lastCheckpoint != null) {
			Entity entity = data.getLevel().getEntity(lastCheckpoint.getEntityId());
			if (entity != null) {
				return lastCheckpoint.getYaw();
			}
		}
		return original;
	}

	@Inject(method = "restoreFrom", at = @At("TAIL"))
	private void restoreGD(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
		var thisDuck = (PlayerDuck) this;
		var thisData = thisDuck.geometryDash$getGDData();
		var thatDuck = (PlayerDuck) that;
		var thatData = thatDuck.geometryDash$getGDData();

		thisData.copyFrom(thatData);
		thisData.syncS2C(Set.of(ServerPlayer.class.cast(this)));
	}
}
