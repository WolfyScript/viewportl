package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class WolfyUtilsStackIdentifier implements StackIdentifier {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(((WolfyCoreCommon) WolfyCore.getInstance()).plugin, "custom_item");
    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("wolfyutils");

    private final NamespacedKey namespacedKey;

    public WolfyUtilsStackIdentifier(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * Gets the stack this identifier references.
     * It uses the {@link CustomItem#create()} method to create the stack, or returns null if the referenced {@link CustomItem} is unavailable.
     *
     * @return The referenced ItemStack or null if referenced {@link CustomItem} is unavailable
     */
    @Override
    public ItemStack stack(ItemCreateContext context) {
        return customItem().map(customItem -> customItem.create(context.amount())).orElseGet(() -> {
            WolfyCore.getInstance().getWolfyUtils().getLogger().warning("Couldn't find CustomItem for " + namespacedKey.toString());
            return null;
        });
    }

    /**
     * Gets the {@link CustomItem} this identifier references.
     *
     * @return The referenced {@link CustomItem} of this identifier
     */
    public Optional<CustomItem> customItem() {
        return Optional.ofNullable(((WolfyCoreCommon) WolfyCore.getInstance()).getRegistries().getCustomItems().get(namespacedKey));
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (other == null) return false;
        var itemMeta = other.getItemMeta();
        if (itemMeta == null) return false;
        var container = itemMeta.getPersistentDataContainer();
        if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
            return Objects.equals(this.namespacedKey, BukkitNamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
        }
        return false;
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<WolfyUtilsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<WolfyUtilsStackIdentifier> from(ItemStack itemStack) {
            if (itemStack == null) return Optional.empty();
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Optional.of(new WolfyUtilsStackIdentifier(BukkitNamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING))));
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
                    Component.text("WolfyUtils").color(NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.CRAFTING_TABLE)
            );
        }
    }


}
