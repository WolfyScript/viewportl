package com.wolfyscript.utilities.common.gui;

import java.util.Set;

public interface DragInteractionDetails extends InteractionDetails {

    Set<Integer> getInventorySlots();

    Set<Integer> getRawSlots();

    DragType getType();

}
