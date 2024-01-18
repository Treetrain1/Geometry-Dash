package me.treetrain1.geometrydash.mixin.gravity;

import gravity_changer.GravityComponent;
import me.treetrain1.geometrydash.block.JumpPadBlock;
import me.treetrain1.geometrydash.duck.GravityDuck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GravityComponent.class)
public class GravityComponentMixin implements GravityDuck {

	@Shadow
	@Final
	public Entity entity;

	@Unique
	@Nullable
	private JumpPadBlock.JumpPadType geometryDash$padType = null;

	@Inject(method = "applyGravityChange", at = @At("TAIL"), remap = false)
	private void gdJump(CallbackInfo ci) {
		if (this.geometryDash$padType != null && this.entity instanceof LivingEntity living) {
			JumpPadBlock.Companion.applyDelta(living, this.geometryDash$padType);
			this.geometryDash$padType = null;
		}
	}

	@Override
	public void geometryDash$queueJump(@NotNull JumpPadBlock.JumpPadType type) {
		this.geometryDash$padType = type;
	}
}
