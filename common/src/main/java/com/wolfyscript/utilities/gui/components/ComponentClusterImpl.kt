package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.rendering.PropertyPosition;
import com.wolfyscript.utilities.gui.rendering.RenderProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@KeyedStaticId(key = "cluster")
public class ComponentClusterImpl extends AbstractComponentImpl implements ComponentCluster {

    private int width;
    private int height;
    private final List<Component> children;

    public ComponentClusterImpl(String internalID, WolfyUtils wolfyUtils, Component parent, RenderProperties properties, List<Component> children) {
        super(internalID, wolfyUtils, parent, properties);
        this.children = children;

        int topLeft = 54;
        this.width = 1;
        this.height = Math.abs((topLeft / 9)) + 1;
    }

    public ComponentClusterImpl(ComponentClusterImpl staticComponent) {
        super(staticComponent.getID(), staticComponent.getWolfyUtils(), staticComponent.parent(), staticComponent.properties());
        this.children = staticComponent.children;
    }

    @Override
    public Set<? extends Component> childComponents() {
        return new HashSet<>(children);
    }

    @Override
    public Optional<? extends Component> getChild(String id) {
        return Optional.empty();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public void remove(@NotNull ViewRuntimeImpl viewRuntimeImpl, long nodeId, long parentNode) {
        viewRuntimeImpl.getRenderingGraph().removeNode(nodeId);
    }

    @Override
    public void insert(@NotNull ViewRuntimeImpl viewRuntimeImpl, long parentNode) {
        long id = viewRuntimeImpl.getRenderingGraph().addNode(this);
        viewRuntimeImpl.getRenderingGraph().insertNodeChild(id, parentNode);

        for (Component child : children) {
            if (child instanceof Renderable renderable) {
                renderable.insert(viewRuntimeImpl, id);
            }
        }
    }
}
