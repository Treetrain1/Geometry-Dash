package me.treetrain1.geometrydash.data.mode;

import me.treetrain1.geometrydash.data.GDData;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractGDModeData {
	protected GDData gdData;

	public abstract void tick();

	public abstract void onJump();

	public abstract void onFall();

	public abstract void onLand();

	public abstract float getModelPitch(float tickDelta);

	public abstract void save(CompoundTag compound);

	public abstract void load(CompoundTag compound);

	public void setGdData(GDData gdData) {
		this.gdData = gdData;
	}

}
