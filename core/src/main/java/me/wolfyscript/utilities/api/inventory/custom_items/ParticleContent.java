package me.wolfyscript.utilities.api.inventory.custom_items;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleAnimationUtils;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.LinkedHashMap;

public class ParticleContent extends LinkedHashMap<ParticleLocation, NamespacedKey> {

    public NamespacedKey getParticleEffect(ParticleLocation action) {
        return get(action);
    }

    public void addParticleEffect(ParticleLocation action, NamespacedKey namespacedKey) {
        put(action, namespacedKey);
    }

    public void spawn(Player player, EquipmentSlot equipmentSlot) {
        ParticleAnimationUtils.spawnOnPlayer(getParticleEffect(ParticleLocation.valueOf(equipmentSlot.name())), player, equipmentSlot);
    }
}
