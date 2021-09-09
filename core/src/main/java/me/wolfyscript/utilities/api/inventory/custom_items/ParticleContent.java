package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ParticleContent {

    private final Map<ParticleLocation, Value> animations = new HashMap<>();

    @Deprecated
    public NamespacedKey getParticleEffect(ParticleLocation action) {
        return animations.get(action).animation.getNamespacedKey();
    }

    @Deprecated
    public void addParticleEffect(ParticleLocation action, NamespacedKey namespacedKey) {
        animations.put(action, new Value(namespacedKey));
    }

    public void setAnimation(ParticleLocation location, NamespacedKey key) {
        animations.put(location, new Value(key));
    }

    public void setAnimation(ParticleLocation location, @NotNull ParticleAnimation animation) {
        animations.put(location, new Value(animation));
    }

    public void spawn(Player player, EquipmentSlot equipmentSlot) {
        ParticleUtils.spawnAnimationOnPlayer(getParticleEffect(ParticleLocation.valueOf(equipmentSlot.name())), player, equipmentSlot);
    }

    public static class Value {

        private final ParticleAnimation animation;

        @JsonCreator
        private Value(@JsonProperty JsonNode animation) {
            if (animation.isTextual()) {
                var key = NamespacedKey.of(animation.asText());
                this.animation = Objects.requireNonNull(Registry.PARTICLE_ANIMATIONS.get(key), "Animation \"" + key + "\" not found!");
            } else {
                this.animation = JacksonUtil.getObjectMapper().convertValue(animation, ParticleAnimation.class);
            }
        }

        private Value(NamespacedKey key) {
            this.animation = Objects.requireNonNull(Registry.PARTICLE_ANIMATIONS.get(key), "Animation \"" + key + "\" not found!");
        }

        private Value(@NotNull ParticleAnimation animation) {
            this.animation = Objects.requireNonNull(animation, "Animation cannot be null!");
        }

        @JsonGetter
        public Object getAnimation() {
            if(animation.getNamespacedKey() != null) {
                return animation.getNamespacedKey();
            }
            return animation;
        }
    }
}
