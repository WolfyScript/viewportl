package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.signal.Signal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationImpl<F extends AnimationFrame> extends AnimationCommonImpl<F> {

    AnimationImpl(Component owner, List<? extends AnimationFrameBuilder<F>> animationFrameBuilders, Signal<?> updateSignal) {
        super(owner, animationFrameBuilders, updateSignal);
        updateSignal.linkTo(this);
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext context) {
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
                        if (owner() instanceof SignalledObject signalledOwner) {
                            signalledOwner.update(viewManager, guiHolder, context); // Last frame should be the original again!
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
