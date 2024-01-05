package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerDuck {

	@Unique
	private GameType gdPrevGameType;

	@Unique
	private GDData gdData = new GDData(Player.class.cast(this), null, 1.0);

	@Unique
	@Override
	public GDData geometryDash$getGDData() {
		return this.isGDMode;
	}

	@Unique
	@Override
	public void geometryDash$setGDMode(boolean gdMode) {
		this.isGDMode = gdMode;

		var player = Player.class.cast(this);
		if (player instanceof ServerPlayer serverPlayer) {
			if (gdMode) {
				this.gdPrevGameType = serverPlayer.gameMode.getGameModeForPlayer();
				serverPlayer.setGameMode(GameType.ADVENTURE);
			} else {
				serverPlayer.setGameMode(this.gdPrevGameType);
			}
		}
	}
}
