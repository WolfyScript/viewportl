package com.wolfyscript.utilities.common.gui;

public interface ClickInteractionDetails extends InteractionDetails {

    boolean isShift();

    boolean isSecondary();

    boolean isPrimary();

    int getSlot();

    int getRawSlot();

    int getHotbarButton();

    ClickType getClickType();

}
