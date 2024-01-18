package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.GuiHolder;
import com.wolfyscript.utilities.gui.ViewRuntime;
import com.wolfyscript.utilities.gui.RenderContext;
import com.wolfyscript.utilities.gui.components.Button;
import com.wolfyscript.utilities.world.items.ItemStackConfig;

public class ButtonAnimationFrameImpl implements ButtonAnimationFrame {

    private final Animation<ButtonAnimationFrame> animation;
    private final ItemStackConfig stack;
    private final int duration;

    protected ButtonAnimationFrameImpl(Animation<ButtonAnimationFrame> animation, int duration, ItemStackConfig stack) {
        this.animation = animation;
        this.duration = duration;
        this.stack = stack;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public Animation<ButtonAnimationFrame> animation() {
        return animation;
    }

    @Override
    public ItemStackConfig stack() {
        return stack;
    }

    @Override
    public void render(ViewRuntime viewRuntime, GuiHolder guiHolder, RenderContext renderContext) {
        renderContext.renderStack(animation.owner().position(), stack, renderContext.createContext(guiHolder, ((Button) animation.owner()).icon().getResolvers()));
    }
}
