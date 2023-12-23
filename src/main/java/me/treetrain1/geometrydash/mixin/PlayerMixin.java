package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {

	@Unique
	private boolean isGDMode = false;

	@Unique
	@Override
	public boolean geometryDash$isGDMode() {
		return this.isGDMode;
	}

	@Unique
	@Override
	public void geometryDash$setGDMode(boolean gdMode) {
		this.isGDMode = gdMode;
	}
}
