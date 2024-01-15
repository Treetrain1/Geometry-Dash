package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerDuck {

	@Unique
	private GDData gdData = new GDData(Player.class.cast(this));

	@Unique
	@Override
	@NotNull
	public GDData geometryDash$getGDData() {
		return this.gdData;
	}
}
