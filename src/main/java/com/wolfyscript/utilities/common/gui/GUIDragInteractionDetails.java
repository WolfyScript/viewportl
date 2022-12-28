package com.wolfyscript.utilities.common.gui;

import java.util.Set;

public interface GUIDragInteractionDetails extends GUIInteractionDetails {

    Set<Integer> getInventorySlots();

    Set<Integer> getRawSlots();

    DragType getType();

}
