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

package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FuelSettings {

    @JsonAlias("allowed_blocks")
    private List<Material> allowedBlocks = new ArrayList<>();
    @JsonAlias({"burn_time", "burntime"})
    private int burnTime = 20;

    public FuelSettings() {

    }

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

}
