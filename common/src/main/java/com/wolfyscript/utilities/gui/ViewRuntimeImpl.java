package com.wolfyscript.utilities.gui;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.callback.TextInputCallback;
import com.wolfyscript.utilities.gui.callback.TextInputTabCompleteCallback;
import com.wolfyscript.utilities.gui.interaction.InteractionHandler;
import com.wolfyscript.utilities.gui.model.UpdateInformation;
import com.wolfyscript.utilities.gui.reactivity.ReactiveSourceImpl;
import com.wolfyscript.utilities.gui.rendering.Renderer;
import com.wolfyscript.utilities.gui.rendering.RenderingGraph;

import java.util.*;
import java.util.function.Function;

public class ViewRuntimeImpl implements ViewRuntime {

    private static long NEXT_ID = Long.MIN_VALUE;

    private final long id;
    private final RenderingGraph renderingGraph;
    private final Renderer<?> renderer;
    private final InteractionHandler interactionHandler;

    private final WolfyUtils wolfyUtils;
    private final Router router;
    private Window currentRoot;
    private final Deque<Window> history;
    private final Set<UUID> viewers;

    private TextInputCallback textInputCallback;
    private TextInputTabCompleteCallback textInputTabCompleteCallback;

    private final ReactiveSourceImpl reactiveSource;

    protected ViewRuntimeImpl(WolfyUtils wolfyUtils, Function<ViewRuntime, RouterBuilder> rootRouter, Set<UUID> viewers) {
        this.wolfyUtils = wolfyUtils;
        this.renderingGraph = new RenderingGraph(this);
        this.reactiveSource = new ReactiveSourceImpl(this);
        this.renderer = wolfyUtils.getCore().platform().guiUtils().createRenderer(this);
        this.interactionHandler = wolfyUtils.getCore().platform().guiUtils().createInteractionHandler(this);
        this.router = rootRouter.apply(this).create(null);

        this.history = new ArrayDeque<>();
        this.viewers = viewers;
        id = NEXT_ID++;
    }

    public InteractionHandler getInteractionHandler() {
        return interactionHandler;
    }

    public RenderingGraph getRenderingGraph() {
        return renderingGraph;
    }

    public ReactiveSourceImpl getReactiveSource() {
        return reactiveSource;
    }

    public void incomingUpdate(UpdateInformation information) {
        interactionHandler.update(information);
        renderer.update(information);
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
            getCurrentMenu().ifPresent(window -> {
                renderer.changeWindow(window);
                interactionHandler.init(window);
                window.open(this);
                setCurrentRoot(window);
            });
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
        Window window = getRouter().open(this, path);
        setCurrentRoot(window);
        renderer.changeWindow(window);
        interactionHandler.init(window);
        wolfyUtils.getCore().platform().scheduler().syncTask(wolfyUtils, renderer::render);
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
