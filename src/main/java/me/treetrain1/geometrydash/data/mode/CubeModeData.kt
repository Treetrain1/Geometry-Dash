package me.treetrain1.geometrydash.data.mode;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CubeModeData extends AbstractGDModeData {
	private float targetCubeRotation;
	private float cubeRotation;
	private float prevCubeRotation;

	@Override
	public void tick() {
		this.prevCubeRotation = this.cubeRotation;
		this.cubeRotation += (this.targetCubeRotation - this.cubeRotation) * 0.25F;
		if (this.targetCubeRotation >= 360F) {
			this.targetCubeRotation -= 360F;
			this.cubeRotation -= 360F;
			this.prevCubeRotation -= 360F;
		}
	}

	@Override
	public void onJump() {
		this.targetCubeRotation += 180F;
	}

	@Override
	public void onFall() {
		if (!this.gdData.isInJump) this.targetCubeRotation += 90F;
	}

	@Override
	public void onLand() {

	}

	@Override
	public float getModelPitch(float tickDelta) {
		return Mth.lerp(tickDelta, this.prevCubeRotation, this.cubeRotation);
	}

	@Override
	public void save(@NotNull CompoundTag compound) {
		compound.putFloat("target_rotation", this.targetCubeRotation);
		compound.putFloat("rotation", this.cubeRotation);
		compound.putFloat("prev_rotation", this.prevCubeRotation);
	}

	@Override
	public void load(@NotNull CompoundTag compound) {
		this.targetCubeRotation = compound.getFloat("target_rotation");
		this.cubeRotation = compound.getFloat("rotation");
		this.prevCubeRotation = compound.getFloat("prev_rotation");
	}
}
