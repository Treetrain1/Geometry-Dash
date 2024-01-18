package me.treetrain1.geometrydash.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import me.treetrain1.geometrydash.entity.pose.GDPoses;
import net.minecraft.world.entity.Pose;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pose.class )
public class PoseMixin {

	//CREDIT TO nyuppo/fabric-boat-example ON GITHUB

	@SuppressWarnings("ShadowTarget")
	@Final
	@Shadow
	@Mutable
	private static Pose[] $VALUES;

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static Pose gd$newPose(String internalName, int internalId) {
		throw new AssertionError("Mixin injection failed - GD PoseMixin.");
	}

	@Inject(method = "<clinit>",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.PUTSTATIC,
			target = "Lnet/minecraft/world/entity/Pose;$VALUES:[Lnet/minecraft/world/entity/Pose;",
			shift = At.Shift.AFTER
		)
	)
	private static void gd$addGDPoses(CallbackInfo ci) {
		var types = new ArrayList<>(Arrays.asList($VALUES));
		var last = types.get(types.size() - 1);

		var cube = gd$newPose("GDCUBE", last.ordinal() + 1);
		GDPoses.CUBE = cube;
		types.add(cube);

		$VALUES = types.toArray(new Pose[0]);
	}
}
