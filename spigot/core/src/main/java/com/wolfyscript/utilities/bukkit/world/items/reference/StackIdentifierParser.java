package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.wolfyscript.utilities.Keyed;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface StackIdentifierParser<T extends StackIdentifier> extends Keyed, Comparable<StackIdentifierParser<?>> {

    int priority();

    default DisplayConfiguration displayConfig() {
        return new DisplayConfiguration.SimpleDisplayConfig(
                Component.text(key().toString()),
                new DisplayConfiguration.MaterialIconSettings(Material.WRITABLE_BOOK)
        );
    }

    Optional<T> from(ItemStack itemStack);

    @Override
    default int compareTo(@NotNull StackIdentifierParser<?> that) {
        return Integer.compare(that.priority(), this.priority());
    }

    interface DisplayConfiguration {

        Component name();

        IconSettings icon();

        interface IconSettings { }

        record StackIconSettings(ItemStack stack) implements IconSettings { }

        record MaterialIconSettings(Material material) implements IconSettings { }

        record SimpleDisplayConfig(Component name, IconSettings icon) implements DisplayConfiguration { }

    }

}
