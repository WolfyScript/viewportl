package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.gui.interaction.DragType;
import com.wolfyscript.utilities.gui.interaction.InteractionResult;
import java.util.Set;

import com.wolfyscript.utilities.gui.interaction.DragInteractionDetails;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DragInteractionDetailsImpl implements DragInteractionDetails {

    private final InventoryDragEvent event;

    public DragInteractionDetailsImpl(InventoryDragEvent event) {
        this.event = event;
    }

    @Override
    public Set<Integer> getInventorySlots() {
        return event.getInventorySlots();
    }

    @Override
    public Set<Integer> getRawSlots() {
        return event.getRawSlots();
    }

    @Override
    public DragType getType() {
        return switch (event.getType()) {
            case EVEN -> DragType.EVEN;
            case SINGLE -> DragType.SINGLE;
        };
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public InteractionResult.ResultType getResultType() {
        return null;
    }
}
