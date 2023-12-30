package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.ItemHelper;
import com.wolfyscript.utilities.gui.ItemHelperImpl;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.functions.SerializableFunction;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import com.wolfyscript.utilities.platform.world.items.Items;

import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonAnimationFrameBuilderImpl implements ButtonAnimationFrameBuilder {

    private final WolfyUtils wolfyUtils;
    private final ItemHelper itemHelper;
    private int duration;
    private ItemStackConfig stack;

    public ButtonAnimationFrameBuilderImpl(WolfyUtils wolfyUtils) {
        this.duration = 1;
        this.wolfyUtils = wolfyUtils;
        this.itemHelper = new ItemHelperImpl(wolfyUtils);
    }

    @Override
    public ButtonAnimationFrameBuilder stack(String itemId, Consumer<ItemStackConfig> config) {
        this.stack = wolfyUtils.getCore().platform().items().createStackConfig(wolfyUtils, itemId);
        config.accept(stack);
        return this;
    }

    @Override
    public ButtonAnimationFrameBuilder stack(SerializableFunction<ItemHelper, ItemStackConfig> config) {
        // TODO
        return this;
    }

    @Override
    public ButtonAnimationFrameBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public ButtonAnimationFrame build(Animation<ButtonAnimationFrame> animation) {
        return new ButtonAnimationFrameImpl(animation, duration, stack);
    }


}
