package me.treetrain1.geometrydash.mixin;

import me.treetrain1.geometrydash.data.GDData;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerDuck {

	@Unique
	private final GDData gdData = new GDData(Player.class.cast(this));

	@Unique
	@Override
	@NotNull
	public GDData geometryDash$getGDData() {
		return this.gdData;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void gd$tick(CallbackInfo ci) {
		this.gdData.tick();
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void gd$jumpFromGround(CallbackInfo ci) {
		this.gdData.isInJump = true;
        if (this.gdData.gdModeData != null)
			this.gdData.gdModeData.onJump();
	}

}
