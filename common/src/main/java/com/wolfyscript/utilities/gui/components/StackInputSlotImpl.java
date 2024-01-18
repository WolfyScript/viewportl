package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.signal.Signal;

import java.util.function.Consumer;

@KeyedStaticId(key = "stack_input_slot")
public class StackInputSlotImpl extends AbstractComponentImpl implements Interactable, StackInputSlot {

    private final Consumer<ItemStack> onValueChange;
    private final InteractionCallback interactionCallback;
    private final Signal<ItemStack> value;

    public StackInputSlotImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Consumer<ItemStack> onValueChange, InteractionCallback interactionCallback, Signal<ItemStack> value, Position position) {
        super(internalID, wolfyUtils, parent, position);
        this.onValueChange = onValueChange;
        this.interactionCallback = (holder, details) -> {
            // TODO
//            if (details instanceof ClickInteractionDetailsImpl clickInteractionDetails) {
//                InventoryClickEvent event = clickInteractionDetails.getClickEvent();
//                InteractionUtils.applyItemFromInteractionEvent(event.getSlot(), event, Set.of(), itemStack -> {
//                    onValueChange.accept(new ItemStackImpl((WolfyUtilsBukkit) holder.getViewManager().getWolfyUtils(), itemStack));
//                });
//            }
            return interactionCallback.interact(holder, details);
        };
        this.value = value;
        value.linkTo(this);
    }

    @Override
    public StackInputSlot construct(GuiHolder holder, ViewRuntime viewRuntime) {
        return this;
    }

    @Override
    public void update(ViewRuntime viewManager, GuiHolder guiHolder, RenderContext renderContext) {
        renderContext.renderStack(position(), value.get());
        ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(this, renderContext.currentOffset() + position().slot());
    }

    @Override
    public void remove(GuiHolder guiHolder, ViewRuntime viewRuntime, RenderContext renderContext) {
        renderContext.renderStack(position(), null);
        ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(null, renderContext.currentOffset() + position().slot());
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
    }

    public Signal<ItemStack> getValue() {
        return value;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, InteractionDetails interactionDetails) {
        return interactionCallback.interact(guiHolder, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Signal<ItemStack> signal() {
        return value;
    }
}
