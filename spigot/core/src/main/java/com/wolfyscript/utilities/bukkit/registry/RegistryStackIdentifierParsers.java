package com.wolfyscript.utilities.bukkit.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.registry.Registries;
import com.wolfyscript.utilities.registry.RegistrySimple;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegistryStackIdentifierParsers extends RegistrySimple<StackIdentifierParser<?>> {

    private List<StackIdentifierParser<?>> priorityIndexedParsers = List.of();
    private final Registries registries;

    public RegistryStackIdentifierParsers(Registries registries) {
        super(new BukkitNamespacedKey(registries.getCore().getWolfyUtils(), "stack_identifier/parsers"), registries, (Class<StackIdentifierParser<?>>)(Object) StackIdentifierParser.class);
        this.registries = registries;
    }

    @Override
    public void register(NamespacedKey namespacedKey, StackIdentifierParser<?> value) {
        if (value != null) {
            Preconditions.checkState(!this.map.containsKey(namespacedKey), "namespaced key '%s' already has an associated value!", namespacedKey);
            map.put(namespacedKey, value);
            reIndexParsers();
        }
    }

    public List<StackIdentifierParser<?>> sortedParsers() {
        return priorityIndexedParsers;
    }

    public StackIdentifier parseIdentifier(ItemStack stack) {
        for (StackIdentifierParser<?> parser : sortedParsers()) {
            Optional<? extends StackIdentifier> identifierOptional = parser.from(stack);
            if (identifierOptional.isPresent()) return identifierOptional.get();
        }
        return new BukkitStackIdentifier(stack);
    }

    public List<StackIdentifierParser<?>> matchingParsers(ItemStack stack) {
        return sortedParsers().stream().sorted().filter(stackIdentifierParser -> stackIdentifierParser.from(stack).isPresent()).toList();
    }

    /**
     *
     *
     * @param stack
     * @return
     */
    public StackReference parseFrom(ItemStack stack) {
        return new StackReference((WolfyCoreCommon) registries.getCore(), parseIdentifier(stack), 1, stack.getAmount(), stack);
    }

    private void reIndexParsers() {
        priorityIndexedParsers = map.values().stream().sorted(Comparator.naturalOrder()).filter(Objects::nonNull).toList();
    }
}
