package com.wolfyscript.utilities.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.animation.Animation;
import com.wolfyscript.utilities.gui.animation.AnimationBuilder;
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrame;
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrameBuilder;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.interaction.InteractionDetails;
import com.wolfyscript.utilities.gui.rendering.RenderProperties;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

@KeyedStaticId(key = "button")
public class ButtonImpl extends AbstractComponentImpl implements Button {

    private final InteractionCallback interactionCallback;
    private final ButtonIcon icon;
    private final Supplier<Optional<Sound>> soundFunction;
    private final Animation<ButtonAnimationFrame> animation;

    ButtonImpl(WolfyUtils wolfyUtils,
               String id,
               Component parent,
               ButtonBuilderImpl.IconBuilderImpl icon,
               Supplier<Optional<Sound>> soundFunction,
               InteractionCallback interactionCallback,
               RenderProperties properties,
               AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder> animation) {
        super(id, wolfyUtils, parent, properties);
        this.icon = icon.create(this);
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
    public Optional<Sound> sound() {
        return soundFunction.get();
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
