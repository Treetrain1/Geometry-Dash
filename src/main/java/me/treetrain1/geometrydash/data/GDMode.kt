package me.treetrain1.geometrydash.data;

import me.treetrain1.geometrydash.data.mode.AbstractGDModeData;
import me.treetrain1.geometrydash.data.mode.CubeModeData;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;

public enum GDMode implements StringRepresentable {
    CUBE(CubeModeData::new),
    SHIP(CubeModeData::new),
    BALL(CubeModeData::new),
    UFO(CubeModeData::new),
    WAVE(CubeModeData::new),
    ROBOT(CubeModeData::new),
    SPIDER(CubeModeData::new),
    SWING(CubeModeData::new);

	private final Supplier<? extends AbstractGDModeData> supplier;

	GDMode(Supplier<? extends AbstractGDModeData> supplier) {
		this.supplier = supplier;
	}

	public Supplier<? extends AbstractGDModeData> getModeDataSupplier() {
		return this.supplier;
	}

	@Contract(pure = true)
	@Override
    public @NotNull String toString() {
		return this.name();
	}

	@Contract(pure = true)
	@Override
	public @NotNull String getSerializedName() {
		return this.name();
	}
}
