/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This class contains settings of particle animations for different locations they can be spawned at. <br>
 * It is used for the CustomItem, and spawns/stops the animations according to their active location.<br>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class ParticleContent {

    private Settings location = null;
    private Settings block = null;
    private Settings entity = null;
    private PlayerSettings player = null;

    public Settings getLocation() {
        return location;
    }

    /**
     * Gets the animation of the {@link PlayerSettings} of the specified {@link EquipmentSlot}.<br>
     * If the equipment slot is null, it will get the parent animation of the {@link PlayerSettings}.
     *
     * @param equipmentSlot The equipment slot to get the animation for, or null for the parent animation.
     * @return The animation for the players' equipment slot, or the parent animation if equipment slot is null.
     */
    public ParticleAnimation getPlayerAnimation(@Nullable EquipmentSlot equipmentSlot) {
        if(equipmentSlot == null) {
            return getAnimation(ParticleLocation.PLAYER);
        }
        return getPlayer() != null ? getPlayer().getByEquipmentSlot(equipmentSlot) : null;
    }

    /**
     * Gets the animation set for the specified {@link ParticleLocation}.<br>
     *
     * <br><b>Note:</b><br>
     * If using the {@link ParticleLocation#PLAYER} it will always return the parent animation of the {@link PlayerSettings}!<br>
     * Use {@link #getPlayerAnimation(EquipmentSlot)} instead if you want to get equipment slot specific animations!
     *
     * @param location The location to get the animation for.
     * @return The animation set for the specified {@link ParticleLocation}
     */
    public ParticleAnimation getAnimation(ParticleLocation location) {
        var setting = switch (location) {
            case BLOCK -> getBlock();
            case PLAYER -> getPlayer();
            case ENTITY -> getEntity();
            case LOCATION -> getLocation();
        };
        return setting != null ? setting.getAnimation() : null;
    }

    /**
     * Sets the animation for the specified {@link ParticleLocation}.<br>
     *
     * <br<<b>Note:</b><br>
     * When using {@link ParticleLocation#PLAYER} it will only set the parent animation.<br>
     * To edit equipment specific animations use {@link #setPlayer(PlayerSettings)}.
     *
     * @param location The {@link ParticleLocation} to set the animation for.
     * @param animation The new animation to set.
     */
    public void setAnimation(ParticleLocation location, ParticleAnimation animation) {
        switch (location) {
            case BLOCK -> setBlock(new Settings(animation));
            case PLAYER -> setPlayer(new PlayerSettings(animation));
            case ENTITY -> setEntity(new Settings(animation));
            case LOCATION -> setLocation(new Settings(animation));
        }
    }

    /**
     * Spawns the animation for the specified equipment slot if it is available.
     *
     * @param player The player to spawn the animation on.
     * @param slot The {@link EquipmentSlot} of the animation.
     */
    public void spawn(Player player, @Nullable EquipmentSlot slot) {
        var animation = getPlayerAnimation(slot);
        if (animation != null) {
            animation.spawn(player, slot);
        }
    }

    public void setLocation(Settings location) {
        this.location = location;
    }

    public Settings getBlock() {
        return block;
    }

    public void setBlock(Settings block) {
        this.block = block;
    }

    public Settings getEntity() {
        return entity;
    }

    public void setEntity(Settings entity) {
        this.entity = entity;
    }

    public PlayerSettings getPlayer() {
        return player;
    }

    public void setPlayer(PlayerSettings player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "ParticleContent{" +
                "location=" + location +
                ", block=" + block +
                ", entity=" + entity +
                ", player=" + player +
                '}';
    }

    public static class Settings {

        private ParticleAnimation animation;

        protected Settings() { }

        public Settings(NamespacedKey key) {
            setAnimation(key);
        }

        public Settings(@NotNull ParticleAnimation animation) {
            setAnimation(animation);
        }

        @JsonGetter
        public ParticleAnimation getAnimation() {
            return animation;
        }

        @JsonAlias("effect")
        @JsonSetter
        public void setAnimation(ParticleAnimation animation) {
            this.animation = Objects.requireNonNull(animation, "Animation cannot be null!");
        }

        public void setAnimation(NamespacedKey animation) {
            this.animation = Objects.requireNonNull(Registry.PARTICLE_ANIMATIONS.get(animation), "Animation \"" + animation + "\" not found!");
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "animation=" + animation +
                    '}';
        }
    }

    /**
     * Contains player specific animations per EquipmentSlot.<br>
     * The animation from the parent value ({@link #getAnimation()}, {@link #setAnimation(ParticleAnimation)}, and {@link #setAnimation(NamespacedKey)}) is used if no equipment slot is selected (see {@link #getPlayerAnimation(EquipmentSlot)}).
     */
    public static class PlayerSettings extends Settings {

        private ParticleAnimation head;
        private ParticleAnimation chest;
        private ParticleAnimation legs;
        private ParticleAnimation feet;
        private ParticleAnimation mainHand;
        private ParticleAnimation offHand;

        public PlayerSettings() {
            super();
        }

        public PlayerSettings(NamespacedKey key) {
            super(key);
        }

        public PlayerSettings(@NotNull ParticleAnimation animation) {
            super(animation);
        }

        public ParticleAnimation getByEquipmentSlot(EquipmentSlot equipmentSlot) {
            return switch (equipmentSlot) {
                case HEAD -> getHead();
                case CHEST -> getChest();
                case LEGS -> getLegs();
                case FEET -> getFeet();
                case HAND -> getMainHand();
                case OFF_HAND -> getOffHand();
            };
        }

        public void setHead(ParticleAnimation head) {
            this.head = head;
        }

        public void setChest(ParticleAnimation chest) {
            this.chest = chest;
        }

        public void setFeet(ParticleAnimation feet) {
            this.feet = feet;
        }

        public ParticleAnimation getHead() {
            return head;
        }

        public ParticleAnimation getChest() {
            return chest;
        }

        public ParticleAnimation getLegs() {
            return legs;
        }

        public void setLegs(ParticleAnimation legs) {
            this.legs = legs;
        }

        public ParticleAnimation getFeet() {
            return feet;
        }

        public ParticleAnimation getMainHand() {
            return mainHand;
        }

        public void setMainHand(ParticleAnimation mainHand) {
            this.mainHand = mainHand;
        }

        public ParticleAnimation getOffHand() {
            return offHand;
        }

        public void setOffHand(ParticleAnimation offHand) {
            this.offHand = offHand;
        }

        @Override
        public String toString() {
            return "PlayerSettings{" +
                    "animation=" + getAnimation() +
                    ", head=" + head +
                    ", chest=" + chest +
                    ", legs=" + legs +
                    ", feet=" + feet +
                    ", mainHand=" + mainHand +
                    ", offHand=" + offHand +
                    '}';
        }
    }
}
