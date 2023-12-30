package com.wolfyscript.utilities.gui;

public enum DragType {
    /**
     * One item from the cursor is placed in each selected slot.
     */
    SINGLE,
    /**
     * The cursor is split evenly across all selected slots, not to exceed the
     * Material's max stack size, with the remainder going to the cursor.
     */
    EVEN
}
