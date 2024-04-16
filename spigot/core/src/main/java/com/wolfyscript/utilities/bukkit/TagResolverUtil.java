package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.plugins.PlaceholderAPIIntegration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public class TagResolverUtil {

    public static TagResolver papi(Player player) {
        return TagResolver.resolver("papi", (args, context) -> {
            String text = args.popOr("The <papi> tag requires exactly one argument, text with papi placeholders!").value();
            PlaceholderAPIIntegration integration = player.getServer().getServicesManager().load(WolfyCoreSpigot.class).getCompatibilityManager().getPlugins().getIntegration("PlaceholderAPI", PlaceholderAPIIntegration.class);
            if (integration != null) {
                text = integration.setPlaceholders(player, text);
            }
            return Tag.inserting(Component.text(text));
        });
    }

    /**
     * Creates a {@link TagResolver}, that will replace tags of the syntax: <br>
     * <code>&lt;list_entry:&lt;index&gt;&gt;</code><br>
     * This will insert the Component from the specified list and index.<br>
     * In case the specific index is outbounds, it'll insert an empty Component.<br>
     * If no index is specified the tag will resolve to the first element of the components list.<br>
     * If the list itself is empty, it'll insert an empty Component.
     *
     * @param descriptionComponents The Components to use for the resolver.
     * @return The new TagResolver that resolves the <code>&lt;entries:&lt;index&gt;&gt;</code> tags.
     */
    public static TagResolver entries(List<Component> descriptionComponents) {
        return entries(descriptionComponents, Component.empty(), 0);
    }

    public static TagResolver entries(List<Component> descriptionComponents, int shift) {
        return entries(descriptionComponents, Component.empty(), shift);
    }

    public static TagResolver entries(List<Component> descriptionComponents, Component emptyComponent, int shift) {
        return entries(descriptionComponents, integer -> Tag.inserting(emptyComponent), () -> Tag.inserting(emptyComponent), shift);
    }

    public static TagResolver entries(List<Component> descriptionComponents, Component outBoundsComponent, Component emptyComponent, int shift) {
        return entries(descriptionComponents, integer -> Tag.inserting(outBoundsComponent), () -> Tag.inserting(emptyComponent), shift);
    }

    public static TagResolver entries(List<Component> descriptionComponents, Function<Integer, Tag> outBoundsTag, Supplier<Tag> emptyTag, int shift) {
        return entries("list_entry", descriptionComponents, outBoundsTag, emptyTag, shift);
    }

    public static TagResolver entries(String tagName, List<Component> descriptionComponents, Function<Integer, Tag> outBoundsTag, Supplier<Tag> emptyTag, int shift) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            if (descriptionComponents.isEmpty()) return emptyTag.get();
            int index = argumentQueue.hasNext() ? argumentQueue.pop().asInt().orElse(0) : 0;
            if (index >= descriptionComponents.size()) return outBoundsTag.apply(index);
            index = (index + shift) % descriptionComponents.size();
            return Tag.inserting(descriptionComponents.get(index));
        });
    }

}
