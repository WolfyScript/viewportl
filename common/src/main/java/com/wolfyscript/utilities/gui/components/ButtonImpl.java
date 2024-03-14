package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.animation.Animation;
import com.wolfyscript.utilities.gui.animation.AnimationBuilder;
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrame;
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrameBuilder;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.rendering.PropertyPosition;
import com.wolfyscript.utilities.gui.rendering.RenderProperties;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@KeyedStaticId(key = "button")
public class ButtonImpl extends AbstractComponentImpl implements Button {

    private final InteractionCallback interactionCallback;
    private final ButtonIcon icon;
    private final Function<GuiHolder, Optional<Sound>> soundFunction;
    private final Animation<ButtonAnimationFrame> animation;

    ButtonImpl(WolfyUtils wolfyUtils,
               String id,
               Component parent,
               ButtonIcon icon,
               Function<GuiHolder, Optional<Sound>> soundFunction,
               InteractionCallback interactionCallback,
               RenderProperties properties,
               AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder> animation) {
        super(id, wolfyUtils, parent, properties);
        this.icon = icon;
        this.interactionCallback = interactionCallback;
        this.soundFunction = soundFunction;
        this.animation = animation != null ? animation.build(this) : null;
    }

    private ButtonImpl(ButtonImpl button) {
        super(button.getID(), button.getWolfyUtils(), button.parent(), button.properties());
        this.interactionCallback = button.interactionCallback;
        this.icon = button.icon;
        this.soundFunction = button.soundFunction;
        this.animation = button.animation; // TODO: Properly copy
    }

    @Override
    public ButtonIcon icon() {
        return icon;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, InteractionDetails interactionDetails) {
        if (parent() instanceof Interactable interactableParent) {
            InteractionResult result = interactableParent.interact(guiHolder, interactionDetails);
            if (result.isCancelled()) return result;
        }
        if (animation != null) {
            animation.updateSignal().update(o -> o);
        }
        soundFunction.apply(guiHolder).ifPresent(sound -> {
            Audience audience = guiHolder.getViewManager().getWolfyUtils().getCore().platform().adventure().player(guiHolder.getPlayer().uuid());
            audience.playSound(sound);
        });

        return interactionCallback.interact(guiHolder, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public void insert(@NotNull ViewRuntimeImpl viewRuntimeImpl, long parentNode) {
        long id = viewRuntimeImpl.getRenderingGraph().addNode(this);
        viewRuntimeImpl.getRenderingGraph().insertNodeChild(id, parentNode);
    }

    @Override
    public void remove(@NotNull ViewRuntimeImpl viewRuntimeImpl, long nodeId, long parentNode) {
        viewRuntimeImpl.getRenderingGraph().removeNode(nodeId);
    }

    public static class DynamicIcon implements ButtonIcon {

        private final ItemStackConfig config;
        private final TagResolver resolvers;

        DynamicIcon(ItemStackConfig config, TagResolver resolvers) {
            this.config = config;
            this.resolvers = resolvers;
        }

        @Override
        public ItemStackConfig getStack() {
            return config;
        }

        public TagResolver getResolvers() {
            return resolvers;
        }

    }

}
