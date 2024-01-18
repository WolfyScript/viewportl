package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.TextInputCallback;
import com.wolfyscript.utilities.gui.callback.TextInputTabCompleteCallback;
import com.wolfyscript.utilities.gui.components.AbstractComponentImpl;

import java.util.*;
import java.util.function.Function;

public class ViewRuntimeImpl implements ViewRuntime {

    private static long NEXT_ID = Long.MIN_VALUE;

    private final long id;
    private final Map<Integer, Component> leaveNodes = new HashMap<>();
    private final Map<UUID, RenderContext> viewerContexts = new HashMap<>();
    private final Set<Effect> updatedSignalsSinceLastUpdate = new HashSet<>();
    private boolean blockedByInteraction = false;

    private final WolfyUtils wolfyUtils;
    private final Router router;
    private Window currentRoot;
    private final Deque<Window> history;
    private final Set<UUID> viewers;

    private TextInputCallback textInputCallback;
    private TextInputTabCompleteCallback textInputTabCompleteCallback;

    protected ViewRuntimeImpl(WolfyUtils wolfyUtils, Function<ViewRuntime, RouterBuilder> rootRouter, Set<UUID> viewers) {
        this.wolfyUtils = wolfyUtils;
        this.router = rootRouter.apply(this).create(null);

        this.history = new ArrayDeque<>();
        this.viewers = viewers;
        id = NEXT_ID++;
    }

    @Override
    public void openNew() {
        openNew(new String[0]);
    }

    @Override
    public void open() {
        if (history.isEmpty()) {
            openNew();
        } else {
            getCurrentMenu().ifPresent(window -> window.open(this));
        }
    }

    @Override
    public void openPrevious() {
        history.poll(); // Remove active current menu
        setCurrentRoot(history.peek());
    }

    public void setCurrentRoot(Window currentRoot) {
        getCurrentMenu().ifPresent(window -> window.close(this));
        this.currentRoot = currentRoot;
    }

    @Override
    public Optional<Window> getCurrentMenu() {
        return Optional.ofNullable(currentRoot);
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    @Override
    public Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    public long getId() {
        return id;
    }

    @Override
    synchronized public void blockedByInteraction() {
        this.blockedByInteraction = true;
    }

    @Override
    synchronized public void unblockedByInteraction() {
        this.blockedByInteraction = false;
    }

    public Optional<Component> getLeaveNode(int slot) {
        return Optional.ofNullable(leaveNodes.get(slot));
    }

    void updateObjects(Set<Effect> objects) {
        if (blockedByInteraction) {
            updatedSignalsSinceLastUpdate.addAll(objects);
            return;
        }
        getCurrentMenu().ifPresent(window -> {
            for (UUID viewer : getViewers()) {
                getRenderContext(viewer).ifPresent(context -> updateSignals(objects, context));
            }
        });
    }

    @Override
    public void updateSignalQueue(RenderContext context) {
        updateSignals(updatedSignalsSinceLastUpdate, context);
        updatedSignalsSinceLastUpdate.clear();
    }

    void updateSignals(Set<Effect> objects, RenderContext context) {
        for (Effect effect : objects) {
            if (effect instanceof AbstractComponentImpl component) {
                context.enterNode(component);
                effect.update(this, context.holder(), context);
                updateLeaveNodes(component, context.currentOffset() + component.position().slot());
            } else {
                effect.update(this, context.holder(), context);
            }
        }
    }

    public void updateLeaveNodes(Component state, int... slots) {
        for (int slot : slots) {
            updateLeaveNodes(state, slot);
        }
    }

    public void updateLeaveNodes(Component state, int slot) {
        if (state == null) {
            leaveNodes.remove(slot);
        } else {
            leaveNodes.put(slot, state);
        }
    }

    @Override
    public Optional<RenderContext> getRenderContext(UUID viewer) {
        return Optional.ofNullable(viewerContexts.get(viewer));
    }

    @Override
    public Optional<TextInputCallback> textInputCallback() {
        return Optional.ofNullable(textInputCallback);
    }

    @Override
    public void setTextInputCallback(TextInputCallback textInputCallback) {
        this.textInputCallback = textInputCallback;
    }

    @Override
    public Optional<TextInputTabCompleteCallback> textInputTabCompleteCallback() {
        return Optional.ofNullable(textInputTabCompleteCallback);
    }

    @Override
    public void setTextInputTabCompleteCallback(TextInputTabCompleteCallback textInputTabCompleteCallback) {
        this.textInputTabCompleteCallback = textInputTabCompleteCallback;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public void openNew(String... path) {
        unblockedByInteraction();
        Window window = getRouter().open(this, path);
        setCurrentRoot(window);
        for (UUID viewer : getViewers()) {
            RenderContext context = wolfyUtils.getCore().platform().guiUtils().createRenderContext(window, this, viewer);
            viewerContexts.put(viewer, context);
            wolfyUtils.getCore().platform().scheduler().syncTask(wolfyUtils, () -> {
                context.openAndRenderMenuFor(this, viewer);
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewRuntimeImpl that = (ViewRuntimeImpl) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
