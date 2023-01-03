package com.wolfyscript.utilities.common.gui;

public interface ClickInteractionDetails<D extends Data> extends InteractionDetails<D> {

    boolean isShift();

    boolean isSecondary();

    boolean isPrimary();

    int getSlot();

    int getRawSlot();

    int getHotbarButton();

    ClickType getClickType();

}
