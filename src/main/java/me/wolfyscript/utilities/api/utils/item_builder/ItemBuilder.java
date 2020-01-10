package me.wolfyscript.utilities.api.utils.item_builder;

import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack currentItem;

    public ItemBuilder(Material material){
        this.currentItem = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack){
        this.currentItem = itemStack;
    }

    public ItemBuilder setAmount(int amount){
        this.currentItem.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta){
        this.currentItem.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setType(Material material){
        this.currentItem.setType(material);
        return this;
    }

    public ItemBuilder addEnchantment(@Nonnull Enchantment ench, int level){
        this.currentItem.addEnchantment(ench, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(@Nonnull Enchantment ench, int level){
        this.currentItem.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder addEnchantments(@Nonnull Map<Enchantment, Integer> enchantments){
        currentItem.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(@Nonnull Map<Enchantment, Integer> enchantments){
        currentItem.addUnsafeEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.addItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public ItemBuilder removeItemFlags(ItemFlag... itemFlags){
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.removeItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public ItemBuilder setDisplayName(String name){
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.setDisplayName(name);
        return setItemMeta(itemMeta);
    }

    public ItemBuilder setLore(List<String> lore){
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.setLore(lore);
        return setItemMeta(itemMeta);
    }

    public ItemBuilder addLoreLine(String line){
        ItemMeta itemMeta = currentItem.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(line);
        return setLore(lore);
    }

    public ItemBuilder addLoreLine(int index, String line){
        ItemMeta itemMeta = currentItem.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(index, line);
        return setLore(lore);
    }

    public ItemStack create(){
        return currentItem;
    }


}
