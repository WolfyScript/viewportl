package com.wolfyscript.utilities.common.gui;

import java.util.Set;

public interface DragInteractionDetails<D extends Data> extends InteractionDetails<D> {

    Set<Integer> getInventorySlots();

    Set<Integer> getRawSlots();

    DragType getType();

}
