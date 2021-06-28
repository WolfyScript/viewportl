package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class FuelSettings {

    @JsonAlias("allowed_blocks")
    private List<Material> allowedBlocks = new ArrayList<>();
    @JsonAlias("burn_time")
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
