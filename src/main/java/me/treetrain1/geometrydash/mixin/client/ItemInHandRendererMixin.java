package me.treetrain1.geometrydash.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import me.treetrain1.geometrydash.duck.PlayerDuck;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Unique
	private static boolean isGD(LivingEntity entity) {
		return entity instanceof PlayerDuck player && player.geometryDash$getGDData().getPlayingGD();
	}

	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"))
	private boolean cancelGDRender(ItemRenderer instance, LivingEntity entity, ItemStack itemStack, ItemDisplayContext diplayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, Level level, int combinedLight, int combinedOverlay, int seed) {
        return !isGD(entity);
    }

	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
	private void cancelGDArmRender(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
		if (isGD(player)) ci.cancel();
	}
}
