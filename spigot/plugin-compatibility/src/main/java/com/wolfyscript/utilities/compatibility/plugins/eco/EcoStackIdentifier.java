package com.wolfyscript.utilities.compatibility.plugins.eco;

import com.willfp.eco.core.items.Items;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

class EcoStackIdentifier implements StackIdentifier, com.wolfyscript.utilities.bukkit.compatibility.plugins.eco.EcoStackIdentifier {

    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("eco");

    private final org.bukkit.NamespacedKey itemKey;

    public EcoStackIdentifier(org.bukkit.NamespacedKey itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = Items.lookup(itemKey.toString()).getItem();
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        var item = Items.getCustomItem(other);
        return item != null && Objects.equals(itemKey, item.getKey());
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    @Override
    public org.bukkit.NamespacedKey itemKey() {
        return itemKey;
    }

    public static class Parser implements StackIdentifierParser<EcoStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<EcoStackIdentifier> from(ItemStack itemStack) {
            if (Items.isCustomItem(itemStack)) {
                var customStack = Items.getCustomItem(itemStack);
                if (customStack != null) {
                    return Optional.of(new EcoStackIdentifier(customStack.getKey()));
                }
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey key() {
            return ID;
        }

        @Override
        public DisplayConfiguration displayConfig() {
            return new DisplayConfiguration.SimpleDisplayConfig(
                    Component.text("Eco").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.EMERALD)
            );
        }
    }

}
