package com.wolfyscript.utilities.gui;

import java.util.Set;

public interface DragInteractionDetails extends InteractionDetails {

    Set<Integer> getInventorySlots();

    Set<Integer> getRawSlots();

    DragType getType();

}
