package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.ReactiveSource;

import java.util.function.Supplier;

public class AnimationBuilderImpl<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> extends AnimationBuilderCommonImpl<F, FB> {

    public AnimationBuilderImpl(ReactiveSource reactiveSource, Supplier<FB> frameBuilderSupplier) {
        super(reactiveSource, frameBuilderSupplier);
    }

    @Override
    public Animation<F> build(Component component) {
        if (updateSignal == null) {
//            component.getID() + "_click_animation_handler", Boolean.TYPE,
            updateSignal = reactiveSource.createSignal(false);
        }

        return new AnimationImpl<>(component, frameBuilders, updateSignal);
    }
}