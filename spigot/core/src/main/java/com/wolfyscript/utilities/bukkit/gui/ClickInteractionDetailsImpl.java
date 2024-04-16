package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.gui.ClickType;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.interaction.ClickInteractionDetails;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInteractionDetailsImpl implements ClickInteractionDetails {

    private final InventoryClickEvent clickEvent;
    private final ClickType clickType;
    private InteractionResult.ResultType resultType;

    ClickInteractionDetailsImpl(InventoryClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        this.clickType = switch (clickEvent.getClick()) {
            case DROP -> ClickType.DROP;
            case CONTROL_DROP -> ClickType.CONTROL_DROP;
            case LEFT -> ClickType.PRIMARY;
            case RIGHT -> ClickType.SECONDARY;
            case SHIFT_LEFT -> ClickType.SHIFT_PRIMARY;
            case SHIFT_RIGHT -> ClickType.SHIFT_SECONDARY;
            case MIDDLE -> ClickType.MIDDLE;
            case CREATIVE -> ClickType.CREATIVE;
            case NUMBER_KEY -> ClickType.NUMBER_KEY;
            case DOUBLE_CLICK -> ClickType.DOUBLE_CLICK;
            case WINDOW_BORDER_LEFT -> ClickType.CONTAINER_BORDER_PRIMARY;
            case WINDOW_BORDER_RIGHT -> ClickType.CONTAINER_BORDER_SECONDARY;
            default -> throw new IllegalStateException("Unexpected value: " + clickEvent.getClick());
        };
        this.resultType = switch (clickEvent.getResult()) {
            case DEFAULT -> InteractionResult.ResultType.DEFAULT;
            case ALLOW -> InteractionResult.ResultType.ALLOW;
            case DENY -> InteractionResult.ResultType.DENY;
        };
    }

    @Override
    public boolean isShift() {
        return clickEvent.isShiftClick();
    }

    @Override
    public boolean isSecondary() {
        return clickEvent.isRightClick();
    }

    @Override
    public boolean isPrimary() {
        return clickEvent.isLeftClick();
    }

    @Override
    public int getSlot() {
        return clickEvent.getSlot();
    }

    @Override
    public int getRawSlot() {
        return clickEvent.getRawSlot();
    }

    @Override
    public int getHotbarButton() {
        return clickEvent.getHotbarButton();
    }

    @Override
    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public boolean isCancelled() {
        return clickEvent.isCancelled();
    }

    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public InteractionResult.ResultType getResultType() {
        return resultType;
    }
}
