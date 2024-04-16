/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FuelSettings {

    @JsonAlias("allowed_blocks")
    private List<Material> allowedBlocks = new ArrayList<>();
    @JsonAlias({"burn_time", "burntime"})
    private int burnTime = 20;

    public FuelSettings() { }

    public FuelSettings(FuelSettings settings) {
        this.allowedBlocks = new ArrayList<>(settings.allowedBlocks);
        this.burnTime = settings.burnTime;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public List<Material> getAllowedBlocks() {
        return allowedBlocks;
    }

    public void setAllowedBlocks(List<Material> allowedBlocks) {
        this.allowedBlocks = allowedBlocks;
    }

    @Override
    public FuelSettings clone() {
        return new FuelSettings(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelSettings that = (FuelSettings) o;
        return burnTime == that.burnTime && Objects.equals(allowedBlocks, that.allowedBlocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedBlocks, burnTime);
    }
}
