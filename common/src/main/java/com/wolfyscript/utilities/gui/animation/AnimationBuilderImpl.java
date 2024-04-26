package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.BuildContext;
import com.wolfyscript.utilities.gui.components.Component;

import java.util.function.Supplier;

public class AnimationBuilderImpl<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> extends AnimationBuilderCommonImpl<F, FB> {

    public AnimationBuilderImpl(BuildContext context, Supplier<FB> frameBuilderSupplier) {
        super(context.getReactiveSource(), frameBuilderSupplier);
    }

    @Override
    public Animation<F> build(Component component) {
        if (updateSignal == null) {
//            component.getID() + "_click_animation_handler", Boolean.TYPE,
            updateSignal = reactiveSource.createSignal(Boolean.class, r -> false);
        }

        return new AnimationImpl<>(component, frameBuilders, updateSignal);
    }
}
