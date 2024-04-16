package com.wolfyscript.utilities.compatibility.plugins.mmoitems;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

class MMOItemsStackIdentifier implements StackIdentifier, com.wolfyscript.utilities.bukkit.compatibility.plugins.mmoitems.MMOItemsStackIdentifier {

    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("mmoitems");

    private final Type itemType;
    private final String itemName;

    public MMOItemsStackIdentifier(Type itemType, String itemName) {
        this.itemType = itemType;
        this.itemName = itemName;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        MMOItem item = MMOItems.plugin.getMMOItem(itemType, itemName);
        if (item == null) return null;
        ItemStack stack = item.newBuilder().buildSilently();
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        var nbtItem = NBTItem.get(other);
        if (nbtItem.hasType()) {
            return Objects.equals(this.itemType, MMOItems.plugin.getTypes().get(nbtItem.getType())) && Objects.equals(this.itemName, nbtItem.getString("MMOITEMS_ITEM_ID"));
        }
        return false;
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<MMOItemsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MMOItemsStackIdentifier> from(ItemStack itemStack) {
            NBTItem nbtItem = NBTItem.get(itemStack);
            if (nbtItem.hasType()) {
                Type type = MMOItems.plugin.getTypes().get(nbtItem.getType());
                String itemId = nbtItem.getString("MMOITEMS_ITEM_ID");
                return Optional.of(new MMOItemsStackIdentifier(type, itemId));
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
                    Component.text("MMOItems").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.IRON_SWORD)
            );
        }
    }

}
