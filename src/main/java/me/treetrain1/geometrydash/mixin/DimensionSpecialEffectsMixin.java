package me.treetrain1.geometrydash.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.treetrain1.geometrydash.GeometryDash;
import me.treetrain1.geometrydash.worldgen.GeometrySpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {

	@Inject(method = "method_29092", at = @At("TAIL"))
	private static void addGDEffects(Object2ObjectArrayMap<ResourceLocation, DimensionSpecialEffects> map, CallbackInfo ci) {
		map.put(GeometryDash.SPECIAL_EFFECTS, new GeometrySpecialEffects());
	}
}
