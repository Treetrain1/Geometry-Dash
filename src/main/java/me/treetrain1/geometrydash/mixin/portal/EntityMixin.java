package me.treetrain1.geometrydash.mixin.portal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.treetrain1.geometrydash.block.CustomPortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Unique
	@Nullable
	private ResourceKey<Level> customPortalFromDimension = null;

	@Unique
	@Nullable
	private ResourceKey<Level> customPortalToDimension = null;

	@Shadow
	public abstract Level level();

	@Inject(method = "handleInsidePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;"))
	private void handleInCustomPortal(BlockPos pos, CallbackInfo ci) {
		var state = this.level().getBlockState(pos);
		if (state.getBlock() instanceof CustomPortalBlock portal) {
			this.customPortalFromDimension = portal.fromDimension;
			this.customPortalToDimension = portal.toDimension;
		}
	}

	@ModifyVariable(method = "handleNetherPortal", at = @At("STORE"), ordinal = 0)
	private ResourceKey<Level> customLevel(ResourceKey<Level> original) {
		if (this.customPortalFromDimension != null && this.customPortalToDimension != null) {
			return this.level().dimension() == this.customPortalToDimension ? this.customPortalFromDimension : this.customPortalToDimension;
		}
		return original;
	}

	@ModifyExpressionValue(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isNetherEnabled()Z"))
	private boolean setCustomEnabled(boolean original, @Local ResourceKey<Level> key) {
		if (key == this.customPortalFromDimension || key == this.customPortalToDimension) {
			return true;
		}
		return original;
	}

	@Inject(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", shift = At.Shift.BY, by = 2))
	private void resetCustomPortal(CallbackInfo ci) {
		this.customPortalFromDimension = null;
		this.customPortalToDimension = null;
	}
}
