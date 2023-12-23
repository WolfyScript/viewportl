package com.wolfyscript.utilities.common.gui;

public interface GUIClickInteractionDetails extends GUIInteractionDetails {

    boolean isShift();

    boolean isSecondary();

    boolean isPrimary();

    int getSlot();

    int getRawSlot();

    int getHotbarButton();

    ClickType getClickType();

}
