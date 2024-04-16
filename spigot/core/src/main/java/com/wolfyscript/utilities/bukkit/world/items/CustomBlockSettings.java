package com.wolfyscript.utilities.bukkit.world.items;

import com.wolfyscript.utilities.Copyable;
import java.util.Objects;

public class CustomBlockSettings implements Copyable<CustomBlockSettings> {

    private boolean useCustomDrops;

    public CustomBlockSettings() {
        this.useCustomDrops = true;
    }

    private CustomBlockSettings(CustomBlockSettings settings) {
        this.useCustomDrops = settings.useCustomDrops;
    }

    public boolean isUseCustomDrops() {
        return useCustomDrops;
    }

    public void setUseCustomDrops(boolean useCustomDrops) {
        this.useCustomDrops = useCustomDrops;
    }

    @Override
    public CustomBlockSettings copy() {
        return new CustomBlockSettings(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomBlockSettings that = (CustomBlockSettings) o;
        return useCustomDrops == that.useCustomDrops;
    }

    @Override
    public int hashCode() {
        return Objects.hash(useCustomDrops);
    }
}
