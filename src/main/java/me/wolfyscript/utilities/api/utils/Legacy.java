package me.wolfyscript.utilities.api.utils;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Locale;

@Deprecated
public class Legacy {

    private static HashMap<String, Enchantment> enchantments = new HashMap<>();

    public static void init() {
        initEnchants();
        initPotions();
    }

    private static void initEnchants() {
        enchantments.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchantments.put("fire_protection", Enchantment.PROTECTION_FIRE);
        enchantments.put("feather_falling", Enchantment.PROTECTION_FALL);
        enchantments.put("blast_protection", Enchantment.PROTECTION_EXPLOSIONS);
        enchantments.put("projectile_protection", Enchantment.PROTECTION_PROJECTILE);
        enchantments.put("respiration", Enchantment.OXYGEN);
        enchantments.put("aqua_affinity", Enchantment.WATER_WORKER);
        enchantments.put("sharpness", Enchantment.DAMAGE_ALL);
        enchantments.put("smite", Enchantment.DAMAGE_UNDEAD);
        enchantments.put("bane_of_arthropods", Enchantment.DAMAGE_ARTHROPODS);
        enchantments.put("looting", Enchantment.LOOT_BONUS_MOBS);
        enchantments.put("efficiency", Enchantment.DIG_SPEED);
        enchantments.put("unbreaking", Enchantment.DURABILITY);
        enchantments.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        enchantments.put("power", Enchantment.ARROW_DAMAGE);
        enchantments.put("punch", Enchantment.ARROW_KNOCKBACK);
        enchantments.put("flame", Enchantment.ARROW_FIRE);
        enchantments.put("infinity", Enchantment.ARROW_INFINITE);
        enchantments.put("luck_of_the_sea", Enchantment.LUCK);
        if (WolfyUtilities.hasSpecificUpdate("1_10")) {
            enchantments.put("sweeping", Enchantment.SWEEPING_EDGE);
        }

    }

    /**
     * @param name The in-game name of the enchantment
     * @return null or the Enchantment
     */
    @Deprecated
    public static Enchantment getEnchantment(String name) {
        if (!enchantments.containsKey(name)) {
            return Enchantment.getByName(name);
        }
        return enchantments.get(name);
    }

    @Deprecated
    public static String getEnchantName(Enchantment enchantment) {
        if (!enchantments.containsValue(enchantment)) {
            return enchantment.getName().toLowerCase(Locale.ENGLISH);
        }
        for (String name : enchantments.keySet()) {
            if (enchantments.get(name).equals(enchantment)) {
                return name;
            }
        }
        return "";
    }

    private static HashMap<String, PotionEffectType> potions = new HashMap<>();

    @Deprecated
    private static void initPotions() {
        potions.put("slowness", PotionEffectType.SLOW);
        potions.put("haste", PotionEffectType.FAST_DIGGING);
        potions.put("mining_fatigue", PotionEffectType.SLOW_DIGGING);
        potions.put("strength", PotionEffectType.INCREASE_DAMAGE);
        potions.put("instant_health", PotionEffectType.HEAL);
        potions.put("instant_damage", PotionEffectType.HARM);
        potions.put("jump_boost", PotionEffectType.JUMP);
        potions.put("nausea", PotionEffectType.CONFUSION);
        potions.put("resistance", PotionEffectType.DAMAGE_RESISTANCE);
    }

    @Deprecated
    public static PotionEffectType getPotion(String name) {
        if (!potions.containsKey(name)) {
            return PotionEffectType.getByName(name);
        }
        return potions.get(name);
    }

    @Deprecated
    public static String getPotionName(PotionEffectType potion) {
        if (!potions.containsValue(potion)) {
            return potion.getName().toLowerCase(Locale.ENGLISH);
        }
        for (String name : potions.keySet()) {
            if (potions.get(name).equals(potion)) {
                return name;
            }
        }
        return "";
    }
}