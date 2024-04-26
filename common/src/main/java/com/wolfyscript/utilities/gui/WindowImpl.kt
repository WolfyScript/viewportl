package com.wolfyscript.utilities.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.interaction.InteractionDetails;
import com.wolfyscript.utilities.platform.scheduler.Task;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.*;

@KeyedStaticId(key = "window")
public final class WindowImpl implements Window {

    private final String id;
    private final Router router;
    private final WolfyUtils wolfyUtils;
    private final Integer size;
    private final WindowType type;
    private net.kyori.adventure.text.Component title;
    private final InteractionCallback interactionCallback;
    final Collection<Component> componentsToRender;

    // Intervalls
    final List<Pair<Runnable, Long>> intervalRunnables = new ArrayList<>();
    final List<Task> intervalTasks = new ArrayList<>();

    WindowImpl(String id,
               Router router,
               Integer size,
               WindowType type,
               net.kyori.adventure.text.Component title,
               InteractionCallback interactionCallback,
               Collection<Component> components) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(interactionCallback);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.id = id;
        this.router = router;
        this.wolfyUtils = router.getWolfyUtils();
        this.size = size;
        this.type = type;
        this.interactionCallback = interactionCallback;
        this.componentsToRender = components;
        this.title = title;
    }

    @Override
    public void open(ViewRuntime viewRuntime) {
        for (Task intervalTask : intervalTasks) {
            intervalTask.cancel();
        }
        intervalTasks.clear();
        for (Pair<Runnable, Long> intervalRunnable : intervalRunnables) {
            Task task = wolfyUtils.getCore().getPlatform().getScheduler().task(wolfyUtils)
                    .interval(intervalRunnable.getValue())
                    .delay(1).execute(intervalRunnable.getKey()).build();
            intervalTasks.add(task);
        }

        // Build graph
        ViewRuntimeImpl runtime = (ViewRuntimeImpl) viewRuntime;
        for (Component component : componentsToRender) {
            if (component instanceof Renderable renderable) {
                renderable.insert(runtime, 0);
            }
        }
    }

    @Override
    public void close(ViewRuntime viewRuntime) {
        for (Task intervalTask : intervalTasks) {
            intervalTask.cancel();
        }
        intervalTasks.clear();

        ViewRuntimeImpl runtime = (ViewRuntimeImpl) viewRuntime;
        runtime.getRenderingGraph().removeNode(0);
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Router router() {
        return router;
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Set<? extends Component> childComponents() {
        return Set.of();
    }

    @Override
    public Optional<Component> getChild(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getSize() {
        return Optional.ofNullable(size);
    }

    @Override
    public Optional<WindowType> getType() {
        return Optional.ofNullable(type);
    }

    public net.kyori.adventure.text.Component title() {
        return title;
    }

    public void title(net.kyori.adventure.text.Component title) {
        this.title = title;
    }

    @Override
    public int width() {
        return size / height();
    }

    @Override
    public int height() {
        return size / 9;
    }

}
