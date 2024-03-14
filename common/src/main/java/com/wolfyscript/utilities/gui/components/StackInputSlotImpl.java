package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.rendering.PropertyPosition;
import com.wolfyscript.utilities.gui.rendering.RenderProperties;
import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@KeyedStaticId(key = "stack_input_slot")
public class StackInputSlotImpl extends AbstractComponentImpl implements Interactable, StackInputSlot {

    private final Consumer<ItemStack> onValueChange;
    private final InteractionCallback interactionCallback;
    private final ItemStack value;

    public StackInputSlotImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Consumer<ItemStack> onValueChange, InteractionCallback interactionCallback, ItemStack value, RenderProperties properties) {
        super(internalID, wolfyUtils, parent, properties);
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
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
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
    public ItemStack value() {
        return value;
    }

    @Override
    public void remove(@NotNull ViewRuntimeImpl viewRuntimeImpl, long nodeId, long parentNode) {
        viewRuntimeImpl.getRenderingGraph().removeNode(nodeId);
    }

    @Override
    public void insert(@NotNull ViewRuntimeImpl viewRuntimeImpl, long parentNode) {
        long id = viewRuntimeImpl.getRenderingGraph().addNode(this);
        viewRuntimeImpl.getRenderingGraph().insertNodeChild(id, parentNode);
    }
}
