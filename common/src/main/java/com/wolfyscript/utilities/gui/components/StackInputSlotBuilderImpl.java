package com.wolfyscript.utilities.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.rendering.PropertyPosition;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.reactivity.Signal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@KeyedStaticId(key = "stack_input_slot")
@ComponentBuilderSettings(base = StackInputSlotBuilder.class, component = StackInputSlot.class)
public class StackInputSlotBuilderImpl extends AbstractComponentBuilderImpl<StackInputSlot, Component> implements StackInputSlotBuilder {

    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.cancel(false);
    private Consumer<ItemStack> onValueChange;
    private Signal<ItemStack> valueSignal;

    @JsonCreator
    protected StackInputSlotBuilderImpl(@JsonProperty("id") String id, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("position") PropertyPosition position) {
        super(id, wolfyUtils, position);
    }

    @Override
    public StackInputSlotBuilder onValueChange(Consumer<ItemStack> onValueChange) {
        this.onValueChange = onValueChange;
        return this;
    }

    public StackInputSlotBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null, "InteractionCallback must be non-null!");
        this.interactionCallback = interactionCallback;
        return this;
    }

    public StackInputSlotBuilder value(Signal<ItemStack> valueSignal) {
        this.valueSignal = valueSignal;
        return this;
    }

    @Override
    public @NotNull StackInputSlot create(Component component) {




        return new StackInputSlotImpl(id(), wolfyUtils, component, onValueChange, interactionCallback, valueSignal.get(), null /* TODO */);
    }
}
