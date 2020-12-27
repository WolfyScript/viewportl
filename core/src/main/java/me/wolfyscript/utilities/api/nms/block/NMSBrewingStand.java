package me.wolfyscript.utilities.api.nms.block;

public interface NMSBrewingStand {

    /**
     * @return The actual FuelLevel from the TileEntity
     */
    int getFuelLevel();

    /**
     * Sets the actual Fuel Level to the TileEntity.
     * This method directly changes the value other than Bukkit, which edits a copy of the TileEntity.
     *
     * @param fuelLevel The fuel level
     */
    void setFuelLevel(int fuelLevel);

    /**
     * @return The actual Brewing time from the TileEntity
     */
    int getBrewingTime();

    /**
     * Sets the actual Brewing time to the TileEntity.
     * This method directly changes the value other than Bukkit, which edits a copy of the TileEntity.
     *
     * @param brewTime The Brewing time in ticks
     */
    void setBrewingTime(int brewTime);
}
