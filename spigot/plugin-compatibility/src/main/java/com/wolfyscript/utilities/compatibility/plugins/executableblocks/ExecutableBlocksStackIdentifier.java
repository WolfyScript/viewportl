package com.wolfyscript.utilities.compatibility.plugins.executableblocks;

import com.ssomar.executableblocks.executableblocks.ExecutableBlocksManager;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ExecutableBlocksIntegration;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

class ExecutableBlocksStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("executableblocks");

    private final ExecutableBlocksIntegration integration;
    private final ExecutableBlocksManager manager;
    private final String id;

    public ExecutableBlocksStackIdentifier(ExecutableBlocksIntegration integration, ExecutableBlocksManager manager, String id) {
        this.id = id;
        this.manager = manager;
        this.integration = integration;
    }

    private ExecutableBlocksStackIdentifier(ExecutableBlocksStackIdentifier other) {
        this.id = other.id;
        this.manager = other.manager;
        this.integration = other.integration;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        return manager.getExecutableBlock(id).map(eb -> eb.buildItem(context.amount(), context.player())).orElseGet(() -> new ItemStack(Material.AIR));
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (!ignoreAmount && other.getAmount() < stack(ItemCreateContext.empty(count)).getAmount() * count) return false;
        return integration.getExecutableBlock(other).map(eB -> eB.equals(id)).orElse(false);
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<ExecutableBlocksStackIdentifier> {

        private final ExecutableBlocksIntegration integration;
        private final ExecutableBlocksManager manager;

        public Parser(ExecutableBlocksIntegration integration, ExecutableBlocksManager manager) {
            this.integration = integration;
            this.manager = manager;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<ExecutableBlocksStackIdentifier> from(ItemStack itemStack) {
            return integration.getExecutableBlock(itemStack).map(ebID -> new ExecutableBlocksStackIdentifier(integration, manager, ebID));
        }

        @Override
        public NamespacedKey key() {
            return ID;
        }
    }

}
