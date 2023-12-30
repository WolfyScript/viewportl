package com.wolfyscript.utilities.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.platform.scheduler.Task;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
public final class WindowImpl implements Window {

    private final String id;
    private final Router router;
    private final WolfyUtils wolfyUtils;
    private final Consumer<WindowDynamicConstructor> rendererConstructor;
    private final Integer size;
    private final WindowType type;
    private String staticTitle = null;
    private SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle;
    private final InteractionCallback interactionCallback;
    final Map<Component, Position> staticComponents;
    final Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents;

    // Intervalls
    final List<Pair<Runnable, Long>> intervalRunnables = new ArrayList<>();
    final List<Task> intervalTasks = new ArrayList<>();

    WindowImpl(String id,
               Router router,
               Integer size,
               WindowType type,
               String staticTitle,
               InteractionCallback interactionCallback,
               Map<Component, Position> staticComponents,
               Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents,
               Consumer<WindowDynamicConstructor> rendererConstructor) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(interactionCallback);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.id = id;
        this.router = router;
        this.wolfyUtils = router.getWolfyUtils();
        this.rendererConstructor = rendererConstructor;
        this.size = size;
        this.type = type;
        this.staticTitle = staticTitle;
        this.interactionCallback = interactionCallback;
        this.staticComponents = staticComponents;
        this.nonRenderedComponents = nonRenderedComponents;
        this.dynamicTitle = null;
    }

    public WindowImpl(WindowImpl staticWindow) {
        this.id = staticWindow.id;
        this.router = staticWindow.router;
        this.wolfyUtils = staticWindow.router.getWolfyUtils();
        this.rendererConstructor = staticWindow.rendererConstructor;
        this.size = staticWindow.size;
        this.type = staticWindow.type;
        this.staticTitle = staticWindow.staticTitle;
        this.dynamicTitle = staticWindow.dynamicTitle;
        this.interactionCallback = staticWindow.interactionCallback;
        this.staticComponents = new HashMap<>(staticWindow.staticComponents);
        this.nonRenderedComponents = new HashMap<>(staticWindow.nonRenderedComponents);
    }

    public WindowImpl dynamicCopy(Map<Component, Position> dynamicComponents,
                                  Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents,
                                  SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle,
                                  List<Pair<Runnable, Long>> intervalRunnables) {
        WindowImpl copy = new WindowImpl(this);
        copy.staticComponents.putAll(dynamicComponents);
        copy.nonRenderedComponents.putAll(nonRenderedComponents);
        copy.dynamicTitle = dynamicTitle;
        copy.intervalRunnables.addAll(intervalRunnables);
        return copy;
    }

    @Override
    public Window construct(GuiHolder holder, GuiViewManager viewManager) {
        var rendererBuilder = new WindowDynamicConstructorImpl(wolfyUtils, holder, this);
        rendererConstructor.accept(rendererBuilder);
        rendererBuilder.usedSignals.forEach((s, signal) -> signal.update(o -> o));
        return rendererBuilder.create(this);
    }

    @Override
    public void open(GuiViewManager guiViewManager) {
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
    public void close(GuiViewManager guiViewManager) {
        for (Task intervalTask : intervalTasks) {
            intervalTask.cancel();
        }
        intervalTasks.clear();
    }

    @Override
    public void render(GuiHolder guiHolder, GuiViewManager viewManager, RenderContext context) {
        if (dynamicTitle != null) {
            context.updateTitle(guiHolder, dynamicTitle.get());
        }

        for (Map.Entry<Component, Position> entry : staticComponents.entrySet()) {
            var position = entry.getValue();
            var component = entry.getKey();
            if (position == null) continue;
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, position.slot());
            context.enterNode(component);
            if (component.construct(guiHolder, viewManager) instanceof SignalledObject signalledObject) {
                signalledObject.update(viewManager, guiHolder, context);
            }
            context.exitNode();
        }
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public Router router() {
        return router;
    }

    @Override
    public RenderContext createContext(GuiViewManager viewManager, UUID player) {
        return getWolfyUtils().getCore().platform().guiUtils().createRenderContext(this, viewManager, player);
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
