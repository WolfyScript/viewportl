package com.wolfyscript.utilities.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.functions.SerializableSupplier;
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
    private String staticTitle = null;
    private final SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle;
    private final InteractionCallback interactionCallback;
    final Collection<Component> componentsToRender;

    // Intervalls
    final List<Pair<Runnable, Long>> intervalRunnables = new ArrayList<>();
    final List<Task> intervalTasks = new ArrayList<>();

    WindowImpl(String id,
               Router router,
               Integer size,
               WindowType type,
               String staticTitle,
               SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle,
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
        this.staticTitle = staticTitle;
        this.interactionCallback = interactionCallback;
        this.componentsToRender = components;
        this.dynamicTitle = dynamicTitle;
    }

    @Override
    public Window construct(GuiHolder holder, ViewRuntime viewManager) {
        return this;
    }

    @Override
    public void open(ViewRuntime viewRuntime) {
        for (Task intervalTask : intervalTasks) {
            intervalTask.cancel();
        }
        intervalTasks.clear();
        for (Pair<Runnable, Long> intervalRunnable : intervalRunnables) {
            Task task = wolfyUtils.getCore().platform().scheduler().task(wolfyUtils)
                    .interval(intervalRunnable.getValue())
                    .delay(1).execute(intervalRunnable.getKey()).build();
            intervalTasks.add(task);
        }
    }

    @Override
    public void close(ViewRuntime viewRuntime) {
        for (Task intervalTask : intervalTasks) {
            intervalTask.cancel();
        }
        intervalTasks.clear();
    }

    @Override
    public void render(GuiHolder guiHolder, ViewRuntime viewManager, RenderContext context) {
        if (dynamicTitle != null) {
            context.updateTitle(guiHolder, dynamicTitle.get());
        }

        for (Component component : componentsToRender) {
            var position = component.position();
            if (position == null) continue;
            ((ViewRuntimeImpl) guiHolder.getViewManager()).updateLeaveNodes(component, position.slot());
            context.enterNode(component);
            component.render(viewManager, guiHolder, context);
            context.exitNode();
        }
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
    public InteractionResult interact(GuiHolder holder, InteractionDetails interactionDetails) {
        return null;
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

    @Override
    public net.kyori.adventure.text.Component createTitle(GuiHolder holder) {
        return wolfyUtils.getChat().getMiniMessage().deserialize(staticTitle);
    }

    public String getStaticTitle() {
        return staticTitle;
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
