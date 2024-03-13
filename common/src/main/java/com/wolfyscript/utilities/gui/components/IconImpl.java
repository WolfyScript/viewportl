package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import org.jetbrains.annotations.NotNull;

public class IconImpl extends AbstractComponentImpl implements Icon {

    private final ItemStackConfig itemStackConfig;

    public IconImpl(WolfyUtils wolfyUtils, String id, Component parent, ItemStackConfig itemStackConfig, Position position) {
        super(id, wolfyUtils, parent, position);
        this.itemStackConfig = itemStackConfig;
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
    public void remove(@NotNull ViewRuntimeImpl viewRuntimeImpl, long nodeId, long parentNode) {

    }

    @Override
    public void insert(@NotNull ViewRuntimeImpl viewRuntimeImpl, long parentNode) {

    }
}
