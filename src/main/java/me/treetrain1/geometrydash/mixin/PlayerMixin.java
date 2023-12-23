package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerDuck {

	@Shadow
	public abstract Abilities getAbilities();

	@Unique
	private Abilities gdAbilitiesCache = new Abilities();

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

		var abilities = this.getAbilities();
		if (gdMode) {
			this.gdAbilitiesCache.invulnerable = abilities.invulnerable;
			this.gdAbilitiesCache.flying = abilities.flying;
			this.gdAbilitiesCache.mayfly = abilities.mayfly;
			this.gdAbilitiesCache.instabuild = abilities.instabuild;
			this.gdAbilitiesCache.mayBuild = abilities.mayBuild;
			this.gdAbilitiesCache.setFlyingSpeed(abilities.getFlyingSpeed());
			this.gdAbilitiesCache.setWalkingSpeed(abilities.getWalkingSpeed());

			this.getAbilities().mayBuild = false;
			this.getAbilities().mayfly = false;
			this.getAbilities().instabuild = false;
			this.getAbilities().invulnerable = false;
			this.getAbilities().flying = false;
		} else {
			abilities.invulnerable = this.gdAbilitiesCache.invulnerable;
			abilities.flying = this.gdAbilitiesCache.flying;
			abilities.mayfly = this.gdAbilitiesCache.mayfly;
			abilities.instabuild = this.gdAbilitiesCache.instabuild;
			abilities.mayBuild = this.gdAbilitiesCache.mayBuild;
			abilities.setFlyingSpeed(this.gdAbilitiesCache.getFlyingSpeed());
			abilities.setWalkingSpeed(this.gdAbilitiesCache.getWalkingSpeed());
		}
	}
}
