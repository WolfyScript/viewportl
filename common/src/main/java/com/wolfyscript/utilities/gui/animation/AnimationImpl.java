package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.reactivity.Effect;
import com.wolfyscript.utilities.gui.reactivity.Signal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationImpl<F extends AnimationFrame> extends AnimationCommonImpl<F> {

    AnimationImpl(Component owner, List<? extends AnimationFrameBuilder<F>> animationFrameBuilders, Signal<?> updateSignal) {
        super(owner, animationFrameBuilders, updateSignal);
    }

    public void render(ViewRuntime viewManager, GuiHolder guiHolder, RenderContext context) {
        context.enterNode(owner());

        AtomicInteger frameDelay = new AtomicInteger(0);
        AtomicInteger frameIndex = new AtomicInteger(0);
        viewManager.getWolfyUtils().getCore().platform().scheduler()
                .task(viewManager.getWolfyUtils())
                .execute(task -> {
                    int delay = frameDelay.getAndIncrement();
                    int frame = frameIndex.get();
                    if (frames().size() <= frame) {
                        task.cancel();
                        if (owner() instanceof Effect signalledOwner) {
                            // TODO
                        }
                        return;
                    }

                    AnimationFrame frameObj = frames().get(frame);
                    if (delay <= frameObj.duration()) {
                        frameObj.render(viewManager, guiHolder, context);
                        return;
                    }
                    frameIndex.incrementAndGet();
                    frameDelay.set(0);
                })
                .interval(1)
                .build();

        context.exitNode();
    }
}
