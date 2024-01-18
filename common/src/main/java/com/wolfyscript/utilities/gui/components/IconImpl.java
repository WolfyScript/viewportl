package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.world.items.ItemStackConfig;

public class IconImpl extends AbstractComponentImpl implements Icon {

    private final ItemStackConfig itemStackConfig;

    public IconImpl(WolfyUtils wolfyUtils, String id, Component parent, ItemStackConfig itemStackConfig, Position position) {
        super(id, wolfyUtils, parent, position);
        this.itemStackConfig = itemStackConfig;
    }

    @Override
    public Icon construct(GuiHolder holder, ViewRuntime viewRuntime) {
        return this;
    }

    @Override
    public void remove(GuiHolder guiHolder, ViewRuntime viewRuntime, RenderContext renderContext) {
        renderContext.renderStack(position(), null);
        //((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, renderContext.currentOffset() + position().slot());
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
    public ItemStackConfig getItemStackConfig() {
        return itemStackConfig;
    }

    @Override
    public void update(ViewRuntime viewManager, GuiHolder guiHolder, RenderContext renderContext) {

    }
}
