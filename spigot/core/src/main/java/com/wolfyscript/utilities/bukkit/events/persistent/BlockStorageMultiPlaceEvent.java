package com.wolfyscript.utilities.bukkit.events.persistent;

import com.google.common.collect.ImmutableList;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockStorageMultiPlaceEvent extends BlockStoragePlaceEvent implements BlockStorageEvent {

    private final List<BlockState> states;
    private final BlockStorage blockStorage;
    private final List<BlockStorage> blockStorages;

    public BlockStorageMultiPlaceEvent(@NotNull List<BlockState> states, List<BlockStorage> blockStorages, @NotNull Block clicked, @NotNull ItemStack itemInHand, @NotNull Player thePlayer, boolean canBuild, EquipmentSlot hand) {
        super(states.get(0).getBlock(), blockStorages.get(0), states.get(0), clicked, itemInHand, thePlayer, canBuild, hand);
        this.states = ImmutableList.copyOf(states);
        this.blockStorage = blockStorages.get(0);
        this.blockStorages = blockStorages;
    }

    public List<BlockStorage> getBlockStorages() {
        return blockStorages;
    }

    /**
     * Gets a list of blockstates for all blocks which were replaced by the
     * placement of the new blocks. Most of these blocks will just have a
     * Material type of AIR.
     *
     * @return immutable list of replaced BlockStates
     */
    @NotNull
    public List<BlockState> getReplacedBlockStates() {
        return states;
    }

    @Override
    public BlockStorage getStorage() {
        return blockStorage;
    }
}
