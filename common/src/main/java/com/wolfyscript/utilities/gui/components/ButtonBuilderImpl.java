package com.wolfyscript.utilities.gui.components;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.animation.*;
import com.wolfyscript.utilities.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.gui.functions.SerializableFunction;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@KeyedStaticId(key = "button")
@ComponentBuilderSettings(base = ButtonBuilder.class, component = Button.class)
public class ButtonBuilderImpl extends AbstractComponentBuilderImpl<Button, Component> implements ButtonBuilder {

    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.cancel(true);
    private Function<GuiHolder, Optional<Sound>> soundFunction = holder -> Optional.of(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 0.25f, 1));;
    private final IconBuilderImpl iconBuilder;
    private AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder> animationBuilder;
    private final ReactiveSource reactiveSource;

    /**
     * Constructor used for non-config setups using Guice injection.
     *
     * @param id The id of the button.
     * @param wolfyUtils The wolfyutils that this button belongs to.
     */
    @Inject
    private ButtonBuilderImpl(String id, WolfyUtils wolfyUtils, Position position, ReactiveSource reactiveSource) {
        super(id, wolfyUtils, position);
        this.iconBuilder = new IconBuilderImpl(wolfyUtils);
        this.reactiveSource = reactiveSource;
    }

    @JsonCreator
    public ButtonBuilderImpl(@JsonProperty("id") String id,
                             @JsonProperty("icon") IconBuilderImpl iconBuilder,
                             @JsonProperty("position") Position position,
                             @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils,
                             @JacksonInject("reactiveSrc") ReactiveSource reactiveSource) {
        super(id, wolfyUtils, position);
        this.iconBuilder = iconBuilder;
        this.reactiveSource = reactiveSource;
    }

    @Override
    public ButtonBuilder icon(Consumer<IconBuilder> consumer) {
        consumer.accept(iconBuilder);
        return this;
    }

    @Override
    public ButtonBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null, "InteractionCallback must be non-null!");
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public ButtonBuilder sound(Function<GuiHolder, Optional<Sound>> soundFunction) {
        Preconditions.checkArgument(soundFunction != null, "Sound function must be non-null!");
        this.soundFunction = soundFunction;
        return this;
    }

    @Override
    public ButtonBuilder animation(Consumer<AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder>> animationBuild) {
        AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder> builder = new AnimationBuilderImpl<>(reactiveSource, () -> new ButtonAnimationFrameBuilderImpl(getWolfyUtils()));
        animationBuild.accept(builder);
        this.animationBuilder = builder;
        return this;
    }

    @Override
    public Button create(Component parent) {
        ButtonImpl button = new ButtonImpl(getWolfyUtils(), id(), parent, iconBuilder.create(), soundFunction, interactionCallback, position(), animationBuilder);
        for (Signal<?> signal : iconBuilder.signals) {
            signal.linkTo(button);
        }
        return button;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IconBuilderImpl implements IconBuilder {

        private WolfyUtils wolfyUtils;
        private ItemStackConfig staticStackConfig;
        private final ItemHelper itemHelper = new ItemHelperImpl(wolfyUtils);
        private final List<TagResolver> tagResolvers = new ArrayList<>();
        final Set<Signal<?>> signals = new HashSet<>();

        @Inject
        private IconBuilderImpl(@JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
            // Used for non-config setups
            this.wolfyUtils = wolfyUtils;
        }

        /**
         * Constructor for reading the icon builder from config.
         *
         * @param staticStackConfig The necessary stack config.
         */
        @JsonCreator
        public IconBuilderImpl(@JsonProperty("stack") ItemStackConfig staticStackConfig) {
            this.staticStackConfig = staticStackConfig;
        }

        @JsonSetter("stack")
        private void setStack(ItemStackConfig config) {
            this.staticStackConfig = config;
        }

        @Override
        public IconBuilder stack(String itemId, Consumer<ItemStackConfig> configure) {
            this.staticStackConfig = wolfyUtils.getCore().platform().items().createStackConfig(wolfyUtils, itemId);
            configure.accept(staticStackConfig);
            return this;
        }

        @Override
        public IconBuilder stack(SerializableFunction<ItemHelper, ItemStackConfig> stackConfigSupplier) {
            // TODO
            return this;
        }

        @Override
        public IconBuilder updateOnSignals(Signal<?>... signals) {
            this.tagResolvers.addAll(Arrays.stream(signals)
                    .map(signal -> TagResolver.resolver(signal.tagName(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get())))))
                    .toList());
            this.signals.clear();
            this.signals.addAll(List.of(signals));
            return this;
        }

        public IconBuilder addTagResolver(TagResolver... tagResolvers) {
            this.tagResolvers.add(TagResolver.resolver(tagResolvers));
            return this;
        }

        @Override
        public ButtonIcon create() {
            return new ButtonImpl.DynamicIcon(staticStackConfig, TagResolver.resolver(tagResolvers));
        }



    }

}
