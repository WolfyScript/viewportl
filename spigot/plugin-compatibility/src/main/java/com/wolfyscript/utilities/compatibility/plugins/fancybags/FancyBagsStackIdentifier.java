package com.wolfyscript.utilities.compatibility.plugins.fancybags;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.chickenstyle.backpack.Backpack;
import me.chickenstyle.backpack.Utils;
import me.chickenstyle.backpack.configs.CustomBackpacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

class FancyBagsStackIdentifier implements StackIdentifier {

    private static final String ID_TAG = "BackpackID";
    public static final NamespacedKey ID = BukkitNamespacedKey.wolfyutilties("fancybags");

    private final int id;

    public FancyBagsStackIdentifier(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        Backpack bag = CustomBackpacks.getBackpack(id);
        if (bag != null) {
            ItemStack stack = Utils.createBackpackItemStack(bag.getName(), bag.getTexture(), bag.getSlotsAmount(), bag.getId());
            stack.setAmount(context.amount());
            return stack;
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        NBTItem nbtItem = new NBTItem(other);
        if (nbtItem.hasTag(ID_TAG, NBTType.NBTTagInt)) {
            return nbtItem.getInteger(ID_TAG) == id;
        }
        return false;
    }

    @Override
    public NamespacedKey key() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<FancyBagsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<FancyBagsStackIdentifier> from(ItemStack itemStack) {
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasTag(ID_TAG, NBTType.NBTTagInt)) {
                return Optional.of(new FancyBagsStackIdentifier(nbtItem.getInteger(ID_TAG)));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey key() {
            return ID;
        }
    }

}
