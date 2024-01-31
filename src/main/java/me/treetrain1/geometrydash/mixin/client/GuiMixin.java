package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {

	@Final
	@Shadow
	private Minecraft minecraft;

	@Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
	private void cancelGDHeartsRender(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci) {
		if (player instanceof PlayerDuck duck && duck.geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	private void gd$renderHotbar(float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	public void gd$renderExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
	public void gd$renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			ci.cancel();
		}
	}

	@WrapOperation(
		method = "renderPlayerHealth",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0),
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
	)
	public void gd$renderPlayerHealthA(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, Operation<Void> original) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			return;
		}
		original.call(instance, resourceLocation, i, j, k, l);
	}

	@WrapOperation(
		method = "renderPlayerHealth",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1),
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
	)
	public void gd$renderPlayerHealthB(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, Operation<Void> original) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			return;
		}
		original.call(instance, resourceLocation, i, j, k, l);
	}

	@WrapOperation(
		method = "renderPlayerHealth",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2),
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
	)
	public void gd$renderPlayerHealthC(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, Operation<Void> original) {
		if (this.minecraft.player instanceof PlayerDuck playerDuck && playerDuck.geometryDash$getGDData().getPlayingGD()) {
			return;
		}
		original.call(instance, resourceLocation, i, j, k, l);
	}

}
